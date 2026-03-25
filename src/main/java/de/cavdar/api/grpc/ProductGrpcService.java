package de.cavdar.api.grpc;

import de.cavdar.api.domain.ProductInput;
import de.cavdar.api.domain.ProductService;
import de.cavdar.api.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * gRPC API – Binary Protocol Buffers über HTTP/2, Port 9090.
 *
 * Die Klassen ProductServiceGrpc, ProductResponse, GetProductRequest, etc.
 * werden zur Build-Zeit aus product.proto generiert (protobuf-maven-plugin).
 *
 * Charakteristika (im Vergleich zu REST/GraphQL):
 *  - Kein JSON – binäres Wire-Format (Protocol Buffers) → ~10x kleiner, schneller
 *  - Stark typisiert, kompilierzeit-sicher für Client und Server
 *  - HTTP/2: Multiplexing, Bidirectional Streaming möglich
 *  - Kein Browser-Support ohne Proxy (grpc-web)
 *  - Ideal für interne Service-zu-Service-Kommunikation
 *
 * Getestet z.B. mit: grpcurl, BloomRPC, Postman (gRPC), oder Spring gRPC Test
 */
@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService service;

    public ProductGrpcService(ProductService service) {
        this.service = service;
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> observer) {
        service.findById(request.getId())
                .map(p -> ProductResponse.newBuilder().setProduct(toProto(p)).build())
                .ifPresentOrElse(
                        response -> { observer.onNext(response); observer.onCompleted(); },
                        () -> observer.onError(Status.NOT_FOUND
                                .withDescription("Product not found: " + request.getId())
                                .asRuntimeException())
                );
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> observer) {
        var products = service.findAll().stream().map(this::toProto).toList();
        observer.onNext(ListProductsResponse.newBuilder().addAllProducts(products).build());
        observer.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> observer) {
        var input = new ProductInput(request.getName(), request.getDescription(), request.getPrice());
        var created = service.create(input);
        observer.onNext(ProductResponse.newBuilder().setProduct(toProto(created)).build());
        observer.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> observer) {
        var input = new ProductInput(request.getName(), request.getDescription(), request.getPrice());
        service.update(request.getId(), input)
                .map(p -> ProductResponse.newBuilder().setProduct(toProto(p)).build())
                .ifPresentOrElse(
                        response -> { observer.onNext(response); observer.onCompleted(); },
                        () -> observer.onError(Status.NOT_FOUND
                                .withDescription("Product not found: " + request.getId())
                                .asRuntimeException())
                );
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> observer) {
        boolean deleted = service.delete(request.getId());
        observer.onNext(DeleteProductResponse.newBuilder().setSuccess(deleted).build());
        observer.onCompleted();
    }

    // ===== Mapping: Domain → Proto =====

    private de.cavdar.api.grpc.proto.Product toProto(de.cavdar.api.domain.Product p) {
        return de.cavdar.api.grpc.proto.Product.newBuilder()
                .setId(p.getId())
                .setName(p.getName())
                .setDescription(p.getDescription() != null ? p.getDescription() : "")
                .setPrice(p.getPrice())
                .build();
    }
}
