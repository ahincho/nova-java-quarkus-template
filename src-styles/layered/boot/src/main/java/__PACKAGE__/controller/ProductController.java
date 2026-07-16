package __PACKAGE__.controller;

import __PACKAGE__.dto.CreateProductRequest;
import __PACKAGE__.dto.ProductResponse;
import __PACKAGE__.entity.Product;
import __PACKAGE__.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for the {@code Product} resource (Layered style).
 *
 * <p>Delegates every business operation to {@link ProductService};
 * never reaches the repository directly. The
 * {@code LayeredArchitectureTest} enforces this rule at build time.
 */
@Path("/api/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class ProductController {

    private final ProductService service;

    /**
     * Constructs the controller around its single collaborator.
     *
     * @param service the product service (must not be null).
     */
    @Inject
    public ProductController(final ProductService service) {
        this.service = service;
    }

    /**
     * Lists every product.
     *
     * @return the list of products as response DTOs.
     */
    @GET
    public List<ProductResponse> list() {
        return service.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * Looks up a product by id.
     *
     * @param id the product id (path parameter).
     * @return the matching product as a response DTO.
     */
    @GET
    @Path("/{id}")
    public ProductResponse findById(@PathParam("id") final String id) {
        final Product product = service.findById(UUID.fromString(id));
        return ProductResponse.from(product);
    }

    /**
     * Creates a new product.
     *
     * @param request the request body (must carry {@code name} and
     *                {@code price}).
     * @return {@code 201 Created} with the persisted product.
     */
    @POST
    public Response create(final CreateProductRequest request) {
        final Product created = service.create(
                request.name(),
                request.price());
        return Response.status(Response.Status.CREATED)
                .entity(ProductResponse.from(created))
                .build();
    }
}