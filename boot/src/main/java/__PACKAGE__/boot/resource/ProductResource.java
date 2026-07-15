package __PACKAGE__.boot.resource;

import __PACKAGE__.product.application.CreateProductUseCase;
import __PACKAGE__.product.domain.Product;
import __PACKAGE__.product.domain.ProductId;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST surface for the {@code product} bounded context. Demonstrates
 * the canonical JAX-RS handler pattern: each endpoint delegates to a
 * {@link CreateProductUseCase} (application service) and returns a
 * JSON-friendly {@link Map} for the response body.
 *
 * <p>The handler is intentionally framework-thin: validation lives in
 * the domain (via {@link __PACKAGE__.product.domain.ProductName#of} and
 * {@link __PACKAGE__.product.domain.ProductPrice#of}), error mapping
 * lives in {@link DomainExceptionMapper}.
 */
@Path("/api/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class ProductResource {

    private final CreateProductUseCase useCase;

    /**
     * CDI-friendly constructor.
     *
     * @param useCaseArg the product use case, injected by Quarkus.
     */
    @Inject
    public ProductResource(final CreateProductUseCase useCaseArg) {
        this.useCase = useCaseArg;
    }

    /**
     * Creates a new product.
     *
     * @param body the request body; must contain {@code name} (String)
     *             and {@code price} (BigDecimal) keys.
     * @return the created product summary, including the generated id.
     */
    @POST
    public Map<String, Object> create(final Map<String, Object> body) {
        final String name = (String) body.get("name");
        final BigDecimal price = new BigDecimal(body.get("price").toString());
        final Product created = useCase.create(name, price);
        return Map.of(
                "id", created.id().value(),
                "name", created.name().value(),
                "price", created.price().value().toPlainString());
    }

    /**
     * Looks up a product by id.
     *
     * @param id the product id (path parameter).
     * @return the product summary.
     */
    @GET
    @Path("/{id}")
    public Map<String, Object> findById(@PathParam("id") final String id) {
        final Product found = useCase.findById(ProductId.fromString(id));
        return Map.of(
                "id", found.id().value(),
                "name", found.name().value(),
                "price", found.price().value().toPlainString());
    }
}