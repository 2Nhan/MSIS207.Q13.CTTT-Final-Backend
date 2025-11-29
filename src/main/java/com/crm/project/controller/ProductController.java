package com.crm.project.controller;

import com.crm.project.dto.request.ImageUploadRequest;
import com.crm.project.dto.request.MatchingRequest;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.request.ProductUpdateRequest;
import com.crm.project.dto.response.*;
import com.crm.project.internal.PageInfo;
import com.crm.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Tag(
        name = "Products",
        description = "APIs for managing products, importing data, and uploading product images."
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Create a new product with required data and optional image upload.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Multipart form-data with required 'data' JSON and optional 'image' file.",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductCreationRequest.class),
                            examples = @ExampleObject(
                                    name = "Example Product",
                                    summary = "Sample form-data fields",
                                    value = """
                                            name=Vitamin C 500mg
                                            brand=Bidiphar
                                            category=Supplement
                                            price=59.9
                                            quantity=100
                                            status=active
                                            """
                            )
                    )
            ))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Product successfully created",
                                    value = """
                                            {
                                              "code": 201,
                                              "message": "Process succeed",
                                              "data": {
                                                "id": "a5b7d3c1-9d2b-4ac0-b03b-9e47c3e8e21a",
                                                "sku": "PRD-001", 
                                                "name": "Vitamin C 500mg",
                                                "brand": "Bidiphar",
                                                "category": "Supplement",
                                                "quantity": 100,
                                                "status": "active",
                                                "price": 59.90,
                                                "discount": 5.0,
                                                "discountType": "PERCENT",
                                                "tag": "immune,health",
                                                "imageUrl": "https://res.cloudinary.com/example/image/upload/vitamin-c.jpg"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Duplicate SKU or validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Duplicate SKU",
                                            summary = "Product with the same SKU already exists",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Process Failed",
                                                      "error": {
                                                        "code": 2007,
                                                        "errorField": "sku",
                                                        "message": "SKU already exists"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing Required Fields",
                                            summary = "Validation error when required fields are missing",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Process Failed",
                                                      "error": [
                                                        {
                                                          "code": 2009,
                                                          "errorField": "sku",
                                                          "message": "Please fill in sku"
                                                        },
                                                        {
                                                          "code": 2011,
                                                          "errorField": "price",
                                                          "message": "Please fill in price"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "File type is not supported",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unsupported File Type",
                                    summary = "Only JPG, PNG, WEBP allowed",
                                    value = """
                                            {
                                              "code": 415,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1100,
                                                "message": "File type is not supported"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> createProduct(
            @Parameter()
            @ModelAttribute @Valid ProductCreationRequest request) {
        ProductResponse productResponse = productService.createProduct(request);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Import products from CSV",
            description = "Upload a CSV file together with a JSON mapping configuration to import product data.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products imported successfully (valid and invalid entries)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Process succeed",
                                              "data": [
                                                {
                                                  "productId": "123e4567-e89b-12d3-a456-426614174000",
                                                  "sku": "SP-001",
                                                  "productName": "Vitamin C 500mg",
                                                  "category": "Supplement",
                                                  "status": "Active",
                                                  "purchaseUnitPrice": 59000,
                                                  "discount": 5,
                                                  "discountType": "PERCENT",
                                                  "imageUrl": "https://example.com/img-vitamin-c.jpg"
                                                },
                                                {
                                                  "productId": "123e4567-e89b-12d3-a456-426614174001",
                                                  "sku": "SP-002",
                                                  "productName": "Paracetamol 500mg",
                                                  "category": "Thuốc OTC",
                                                  "status": "Active",
                                                  "purchaseUnitPrice": 1500,
                                                  "discount": 0,
                                                  "discountType": "NONE"
                                                }
                                              ],
                                              "error": [
                                                {
                                                  "productId": null,
                                                  "sku": "SP-007",
                                                  "productName": "Ống tiêm 5ml",
                                                  "status": "Active",
                                                  "purchaseUnitPrice": 8000,
                                                  "discount": 5,
                                                  "discountType": "AMOUNT"
                                                },
                                                {
                                                  "productId": null,
                                                  "sku": "SP-008",
                                                  "productName": "Bông y tế",
                                                  "status": "INActive",
                                                  "purchaseUnitPrice": 5000,
                                                  "discount": 0,
                                                  "discountType": "NONE"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid matching structure or missing part",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Wrong matching structure",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Process Failed",
                                                      "error": {
                                                        "code": 1009,
                                                        "message": "Wrong matching, matching request is not suitable to the given file"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing matching part",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Missing request part",
                                                      "error": {
                                                        "code": 1104,
                                                        "message": "Required part 'matching' is not presented"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing file part",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Missing request part",
                                                      "error": {
                                                        "code": 1104,
                                                        "message": "Required part 'file' is not presented"
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "File size limit exceeded",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Process Failed",
                                                      "error": {
                                                          "code": 1101,
                                                          "message": "File size limit is 1MB"
                                                      }
                                                     }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "File type not supported (e.g. non-CSV)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": 415,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1100,
                                                "message": "File type is not supported"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> importProductsFromCsv(@Parameter(
                                                                       description = "JSON mapping between CSV headers and Product fields",
                                                                       required = true,
                                                                       content = @Content(
                                                                               mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                               schema = @Schema(implementation = MatchingRequest.class),
                                                                               examples = @ExampleObject(
                                                                                       name = "Sample matching map",
                                                                                       value = """
                                                                                               {
                                                                                                 "sku_field": "sku",
                                                                                                 "name_field": "name",
                                                                                                 "price_field": "price"
                                                                                               }
                                                                                               """
                                                                               )
                                                                       )
                                                               )
                                                               @RequestPart("matching") MatchingRequest matching,

                                                               @Parameter(
                                                                       description = "CSV file to upload",
                                                                       required = true,
                                                                       content = @Content(
                                                                               mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                                                               schema = @Schema(type = "string", format = "binary")
                                                                       )
                                                               )
                                                               @RequestPart("file") MultipartFile file) throws IOException {
        ImportResultResponse<ProductResponse> productResponses = productService.importProductsFromCsv(matching, file);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponses.getValidList())
                .error(productResponses.getInvalidList())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product details",
            description = "Retrieve detailed information about a specific product by its ID.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "UUID of the product to retrieve",
                            example = "019521fb-dee5-4e0a-a108-e6aea5f6c129",
                            required = true
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Successful product retrieval",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Process succeed",
                                              "data": {
                                                "productId": "019521fb-dee5-4e0a-a108-e6aea5f6c129",
                                                "sku": "SP-1762772322687",
                                                "productName": "Macbook Pro M3",
                                                "description": "Laptop cao cấp cho dân lập trình.",
                                                "productSubtitle": "16inch - 32GRAM - 1TB",
                                                "productBrand": "Apple",
                                                "productCategory": "MacBook",
                                                "quantity": 10,
                                                "status": "Active",
                                                "purchaseUnitPrice": 50000000.00,
                                                "discount": 5.00,
                                                "discountType": "PERCENTAGE",
                                                "imageUrl": "https://res.cloudinary.com/dojckaves/image/upload/v1762772325/project/images/20251110105844_Apple_MacBook-Pro_14-16-inch_10182021.jpg",
                                                "tag": "laptop, macbook, apple"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "NotFoundExample",
                                    summary = "Product not found for given ID",
                                    value = """
                                            {
                                              "code": 404,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1007,
                                                "message": "Product not found"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> getProduct(@PathVariable("id") String id) {
        ProductResponse productResponse = productService.getProduct(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieve paginated and filtered list of products with optional filters for category, status, and price range.",
            parameters = {
                    @Parameter(
                            name = "pageNo",
                            description = "Page number (default = 1)",
                            example = "1",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "pageSize",
                            description = "Number of records per page (default = 10)",
                            example = "10",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "sortBy",
                            description = "Field used for sorting (default = 'sku')",
                            example = "productName",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "sortOrder",
                            description = "Sorting direction: asc or desc (default = asc)",
                            example = "asc",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "category",
                            description = "Filter by product category (optional)",
                            example = "Sofa",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filter by product status (optional)",
                            example = "Active",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "minPrice",
                            description = "Minimum price filter (optional)",
                            example = "1000000",
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "maxPrice",
                            description = "Maximum price filter (optional)",
                            example = "5000000",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "SuccessExample1",
                                                    summary = "Example: Successful product list retrieval (with filter)",
                                                    value = """
                                                            {
                                                              "code": 200,
                                                              "message": "Process succeed",
                                                              "data": [
                                                                {
                                                                  "productId": "014ab9fb-c371-4da6-bf3a-3575a95ce4ae",
                                                                  "sku": "P111",
                                                                  "productName": "Sofa Băng 2 Chỗ Mini",
                                                                  "description": "Sofa nhỏ gọn phù hợp căn hộ studio.",
                                                                  "productSubtitle": "Nhiều màu",
                                                                  "productBrand": "CozyHome",
                                                                  "productCategory": "Sofa",
                                                                  "quantity": 0,
                                                                  "status": "PREORDER",
                                                                  "purchaseUnitPrice": 4200000.00,
                                                                  "discount": 0.00,
                                                                  "discountType": "NONE",
                                                                  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1731100011/sofaB.jpg",
                                                                  "tag": null
                                                                }
                                                              ],
                                                              "pagination": {
                                                                "hasPre": false,
                                                                "hasNext": false,
                                                                "pageNumber": 1,
                                                                "totalPages": 1
                                                              }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "SuccessExample2",
                                                    summary = "Example: Successful product list retrieval (no filter)",
                                                    value = """
                                                            {
                                                              "code": 200,
                                                              "message": "Process succeed",
                                                              "data": [
                                                                {
                                                                  "productId": "833f0984-b4c9-4ac0-83fc-8131eadb7951",
                                                                  "sku": "P113",
                                                                  "productName": "Tủ Đầu Giường Gỗ Sồi",
                                                                  "description": "Tủ gỗ sồi 1 ngăn kéo, phong cách mộc mạc.",
                                                                  "productSubtitle": "Dễ phối nội thất",
                                                                  "productBrand": "WoodLife",
                                                                  "productCategory": "Phụ Kiện Phòng Ngủ",
                                                                  "quantity": 14,
                                                                  "status": "Active",
                                                                  "purchaseUnitPrice": 1650000.00,
                                                                  "discount": 5.00,
                                                                  "discountType": "PERCENT",
                                                                  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1731100013/daugiuongA.jpg",
                                                                  "tag": null
                                                                },
                                                                {
                                                                  "productId": "07e50cb8-4e2b-4346-8a8c-6f7486ee4a1c",
                                                                  "sku": "P118",
                                                                  "productName": "Kệ Tivi Gỗ Walnut",
                                                                  "description": "Kệ tivi màu walnut sang trọng.",
                                                                  "productSubtitle": "2 hộc kéo tiện dụng",
                                                                  "productBrand": "Mộc Decor",
                                                                  "productCategory": "Kệ Tivi",
                                                                  "quantity": 15,
                                                                  "status": "Active",
                                                                  "purchaseUnitPrice": 4500000.00,
                                                                  "discount": 0.00,
                                                                  "discountType": "NONE",
                                                                  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1731100018/ketiviA.jpg",
                                                                  "tag": null
                                                                },
                                                                {
                                                                  "productId": "16fc709e-f714-4136-962b-1387adbd824f",
                                                                  "sku": "P120",
                                                                  "productName": "Thảm Trang Trí Lông Ngắn",
                                                                  "description": "Thảm lông ngắn mềm mại, chống trượt.",
                                                                  "productSubtitle": "Nhiều kích thước",
                                                                  "productBrand": "SoftHome",
                                                                  "productCategory": "Thảm",
                                                                  "quantity": 20,
                                                                  "status": "Active",
                                                                  "purchaseUnitPrice": 2350000.00,
                                                                  "discount": 10.00,
                                                                  "discountType": "PERCENT",
                                                                  "imageUrl": "https://res.cloudinary.com/demo/image/upload/v1731100020/thamA.jpg",
                                                                  "tag": null
                                                                }
                                                              ],
                                                              "pagination": {
                                                                "hasPre": true,
                                                                "hasNext": false,
                                                                "pageNumber": 9,
                                                                "totalPages": 9
                                                              }
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "BadRequestExample",
                                            value = """
                                                    {
                                                      "code": 404,
                                                      "message": "Process Failed",
                                                      "error": {
                                                          "code": 1006,
                                                          "message": "No results"
                                                      }
                                                     }
                                                    """
                                    )
                            )
                    ),
            }
    )
    public ResponseEntity<MyApiResponse> getAllProducts(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                        @RequestParam(required = false, defaultValue = "sku") String sortBy,
                                                        @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(required = false) BigDecimal minPrice,
                                                        @RequestParam(required = false) BigDecimal maxPrice) {
        Page<ProductResponse> productResponseList = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder, category, status, minPrice, maxPrice);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponseList.getContent())
                .pagination(new PageInfo<>(productResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products",
            description = "Search for products by keyword (name, SKU, tag) with pagination support.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "query",
                            description = "Keyword to search in product name, SKU, or tag.",
                            example = "Macbook",
                            required = true
                    ),
                    @Parameter(
                            name = "pageNo",
                            description = "Page number (starting from 1)",
                            example = "1",
                            required = false
                    ),
                    @Parameter(
                            name = "pageSize",
                            description = "Number of products per page",
                            example = "2",
                            required = false
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Successful product search result",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Process succeed",
                                              "data": [
                                                {
                                                  "productId": "019521fb-dee5-4e0a-a108-e6aea5f6c129",
                                                  "sku": "SP-1762772322687",
                                                  "productName": "Macbook Pro M3",
                                                  "productSubtitle": "16inch - 32GRAM - 1TB",
                                                  "productBrand": "Apple",
                                                  "productCategory": "MacBook",
                                                  "quantity": 10,
                                                  "status": "Active",
                                                  "purchaseUnitPrice": 50000000.00,
                                                  "discount": 5.00,
                                                  "discountType": "PERCENTAGE",
                                                  "imageUrl": "https://res.cloudinary.com/dojckaves/image/upload/v1762772325/project/images/20251110105844_Apple_MacBook-Pro_14-16-inch_10182021.jpg"
                                                },
                                                {
                                                  "productId": "781f2e62-4f3e-45c6-9879-edfc3b786a22",
                                                  "sku": "SP-1763038913940",
                                                  "productName": "Macbook M4 Pro",
                                                  "productSubtitle": "15 inch - 64GRam - 1TB",
                                                  "productBrand": "Apple",
                                                  "productCategory": "MacBook",
                                                  "quantity": 180,
                                                  "status": "Active",
                                                  "purchaseUnitPrice": 3800.00,
                                                  "discount": 5.00,
                                                  "discountType": "PERCENT",
                                                  "imageUrl": "https://res.cloudinary.com/dojckaves/image/upload/v1763038915/project/images/20251113130154_macbook_m4_pro.webp"
                                                }
                                              ],
                                              "pagination": {
                                                "hasPre": false,
                                                "hasNext": true,
                                                "pageNumber": 1,
                                                "totalPages": 2
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No results found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "NoResultsExample",
                                    summary = "No products found for given query",
                                    value = """
                                            {
                                              "code": 404,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1006,
                                                "message": "No results"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> searchProducts(@RequestParam(name = "query") String query,
                                                        @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Page<ProductResponse> productResponseList = productService.searchProducts(query, PageRequest.of(pageNumber - 1, pageSize));
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponseList.getContent())
                .pagination(new PageInfo<>(productResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{id}/image")
    @Operation(
            summary = "Upload or update product image",
            description = "Upload or replace the image of a specific product by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Image uploaded successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Successful image upload",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Process succeed",
                                              "data": {
                                                "url": "https://res.cloudinary.com/dojckaves/image/upload/v1764249245/project/images/20251127201403_dirty-barn-3Zh7XStV8uc-unsplash.jpg"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Required file does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "FileMissingExample",
                                    summary = "No file provided in request",
                                    value = """
                                            {
                                              "code": 400,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1105,
                                                "message": "Required file does not exist"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported file type",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "UnsupportedFileTypeExample",
                                    summary = "File format not supported (only JPG, PNG, WEBP)",
                                    value = """
                                            {
                                              "code": 415,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1100,
                                                "message": "File type is not supported"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> uploadProductImage(
            @Parameter(
                    name = "id",
                    description = "UUID of the product to update the image for",
                    example = "019521fb-dee5-4e0a-a108-e6aea5f6c129",
                    required = true
            )
            @PathVariable("id") String id,
            @Parameter(
                    name = "image",
                    description = "Image file (JPG|PNG|WEBP)",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    ),
                    required = true
            )
            @ModelAttribute @Valid ImageUploadRequest image) {
        ImageResponse imageResponse = productService.uploadProductImage(id, image.getImage());
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(imageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update product details",
            description = "Update the information of a specific product by its ID.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "JSON body containing updated product information.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example Request",
                                    summary = "Sample product update request",
                                    value = """
                                            {
                                              "name": "Vitamin C 1000mg",
                                              "description": "Extra strength immune booster",
                                              "subtitle": "Boost your immunity naturally",
                                              "brand": "Bidiphar",
                                              "category": "Supplement",
                                              "quantity": 200,
                                              "status": "Active",
                                              "price": 89.90,
                                              "discount": 10.0,
                                              "discountType": "PERCENT",
                                              "tag": "health,immune"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Product update successful",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Process succeed",
                                              "data": {
                                                "id": "a5b7d3c1-9d2b-4ac0-b03b-9e47c3e8e21a",
                                                "sku": "PRD-001",
                                                "name": "Vitamin C 1000mg",
                                                "category": "Supplement",
                                                "status": "Active",
                                                "price": 89.90,
                                                "discount": 10.0,
                                                "discountType": "PERCENT",
                                                "imageUrl": "https://res.cloudinary.com/example/image/upload/vitamin-c-1000.jpg"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing Required Fields",
                                            summary = "Validation failed when mandatory fields are missing",
                                            value = """
                                                    {
                                                      "code": 400,
                                                      "message": "Process Failed",
                                                      "error": [
                                                        {
                                                          "code": 2011,
                                                          "errorField": "price",
                                                          "message": "Please fill in price"
                                                        },
                                                        {
                                                          "code": 2009,
                                                          "errorField": "sku",
                                                          "message": "Please fill in sku"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "NotFoundExample",
                                    summary = "Product not found by ID",
                                    value = """
                                            {
                                              "code": 404,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1006,
                                                "message": "Product not found"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> updateProduct(@PathVariable("id") String id, @RequestBody @Valid ProductUpdateRequest request) {
        ProductResponse productResponse = productService.updateProduct(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product",
            description = "Remove a specific product from the system by its ID.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "Product deletion successful",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "Product deleted successfully"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found for deletion",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "NotFoundExample",
                                    summary = "Product not found by ID",
                                    value = """
                                            {
                                              "code": 404,
                                              "message": "Process Failed",
                                              "error": {
                                                "code": 1006,
                                                "message": "Product not found"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<MyApiResponse> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Product deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
