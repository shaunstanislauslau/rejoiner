package com.google.api.graphql.rejoiner;

import static com.google.common.truth.Truth.assertThat;

import com.google.api.graphql.rejoiner.Annotations.ExtraTypes;
import com.google.api.graphql.rejoiner.Annotations.GraphModifications;
import com.google.api.graphql.rejoiner.Annotations.Mutations;
import com.google.api.graphql.rejoiner.Annotations.Queries;
import com.google.api.graphql.rejoiner.Greetings.GreetingsRequest;
import com.google.api.graphql.rejoiner.Greetings.GreetingsResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.protobuf.Descriptors.FileDescriptor;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingEnvironmentBuilder;
import graphql.schema.GraphQLFieldDefinition;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link com.google.api.graphql.rejoiner.SchemaModule}. */
@RunWith(JUnit4.class)
public final class SchemaModuleTest {

  private static final Key<Set<GraphQLFieldDefinition>> QUERY_KEY =
      Key.get(new TypeLiteral<Set<GraphQLFieldDefinition>>() {}, Queries.class);
  private static final Key<Set<GraphQLFieldDefinition>> MUTATION_KEY =
      Key.get(new TypeLiteral<Set<GraphQLFieldDefinition>>() {}, Mutations.class);
  private static final Key<Set<FileDescriptor>> EXTRA_TYPE_KEY =
      Key.get(new TypeLiteral<Set<FileDescriptor>>() {}, ExtraTypes.class);
  private static final Key<Set<TypeModification>> MODIFICATION_KEY =
      Key.get(new TypeLiteral<Set<TypeModification>>() {}, GraphModifications.class);

  @Test
  public void schemaModuleShouldProvideEmpty() {
    Injector injector =
        Guice.createInjector(new SchemaModule() {});
    assertThat(injector.getInstance(QUERY_KEY)).isNotNull();
    assertThat(injector.getInstance(MUTATION_KEY)).isNotNull();
    assertThat(injector.getInstance(EXTRA_TYPE_KEY)).isNotNull();
    assertThat(injector.getInstance(MODIFICATION_KEY)).isNotNull();
  }

  @Test
  public void schemaModuleShouldProvideQueryFields() {
    Injector injector =
        Guice.createInjector(
            new SchemaModule() {
              @Query
              GraphQLFieldDefinition greeting =
                  GraphQLFieldDefinition.newFieldDefinition()
                      .name("greeting")
                      .type(Scalars.GraphQLString)
                      .staticValue("hello world")
                      .build();
            });
    assertThat(injector.getInstance(QUERY_KEY)).hasSize(1);
    assertThat(injector.getInstance(MUTATION_KEY)).isEmpty();
    assertThat(injector.getInstance(EXTRA_TYPE_KEY)).isEmpty();
    assertThat(injector.getInstance(MODIFICATION_KEY)).isEmpty();
  }

  @Test
  public void schemaShouldValidateWhenIncludingDataFetchingEnvironment() throws ExecutionException, InterruptedException {
    Injector injector =
        Guice.createInjector(
            new SchemaModule() {
              @Query("hello")
              ListenableFuture<GreetingsResponse> querySnippets(
                  GreetingsRequest request, DataFetchingEnvironment environment) {
                return Futures.immediateFuture(
                    GreetingsResponse.newBuilder().setId(request.getId()).build());
              }
            });
    validateSchema(injector);
  }

  @Test
  public void schemaShouldValidateWhenProvidingJustRequest() throws ExecutionException, InterruptedException {
    Injector injector =
        Guice.createInjector(
            new SchemaModule() {
              @Query("hello")
              ListenableFuture<GreetingsResponse> querySnippets(GreetingsRequest request) {
                return Futures.immediateFuture(
                    GreetingsResponse.newBuilder().setId(request.getId()).build());
              }
            });
    validateSchema(injector);
  }

  @Test
  public void schemaShouldValidateWhenInjectingParameterUsingGuice() throws ExecutionException, InterruptedException {
    Injector injector =
        Guice.createInjector(
            new SchemaModule() {
              @Query("hello")
              ListenableFuture<GreetingsResponse> querySnippets(
                  GreetingsRequest request) {
                return Futures.immediateFuture(
                    GreetingsResponse.newBuilder().setId(request.getId()).build());
              }
            });
    validateSchema(injector);
  }

  private void validateSchema(Injector injector) throws ExecutionException, InterruptedException {
    assertThat(injector.getInstance(QUERY_KEY)).hasSize(1);
    assertThat(injector.getInstance(MUTATION_KEY)).isEmpty();
    assertThat(injector.getInstance(EXTRA_TYPE_KEY)).hasSize(1);
    assertThat(injector.getInstance(MODIFICATION_KEY)).isEmpty();
    Set<GraphQLFieldDefinition> queryFields = injector.getInstance(QUERY_KEY);
    GraphQLFieldDefinition hello = queryFields.iterator().next();
    assertThat(hello.getName()).isEqualTo("hello");
    assertThat(hello.getArguments()).hasSize(1);
    assertThat(hello.getArgument("input")).isNotNull();
    assertThat(hello.getArgument("input").getType().getName())
        .isEqualTo("Input_javatests_com_google_api_graphql_rejoiner_proto_GreetingsRequest");

    assertThat(hello.getType().getName())
        .isEqualTo("javatests_com_google_api_graphql_rejoiner_proto_GreetingsResponse");

    Object result =
        hello
            .getDataFetcher()
            .get(DataFetchingEnvironmentBuilder.newDataFetchingEnvironment().arguments(ImmutableMap.of("input", ImmutableMap.of("id", "123"))).build());

    assertThat(result).isInstanceOf(ListenableFuture.class);
    assertThat(((ListenableFuture<?>) result).get())
        .isEqualTo(GreetingsResponse.newBuilder().setId("123").build());
  }

  @Test(expected = CreationException.class)
  public void schemaModuleShouldFailIfWrongTypeIsAnnotated() {
    Guice.createInjector(
        new SchemaModule() {
          @Query String greeting = "hi";
        });
  }
}