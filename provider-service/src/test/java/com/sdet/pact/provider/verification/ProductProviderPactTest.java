package com.sdet.pact.provider.verification;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.sdet.pact.provider.stubs.ProductProviderStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Provider Verification Tests for Product Service
 */
@Provider("ProductProvider")
@PactFolder("../consumer-service/target/pacts")
@DisplayName("Product Provider Verification Tests")
public class ProductProviderPactTest {

    private ProductProviderStub providerStub;
    private static final int PROVIDER_PORT = 8081;

    @BeforeEach
    void setup(PactVerificationContext context) {
        providerStub = new ProductProviderStub(PROVIDER_PORT);
        providerStub.start();
        context.setTarget(new HttpTestTarget("localhost", PROVIDER_PORT));
    }

    @AfterEach
    void tearDown() {
        if (providerStub != null) {
            providerStub.stop();
        }
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    @DisplayName("Verify PACT interactions")
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * State Handlers
     */

    @State("Products exist in the system")
    public void productsExist() {
        providerStub.addProduct(1, "Laptop", 999.99, "Electronics", true);
        providerStub.addProduct(2, "Mouse", 29.99, "Accessories", true);
    }

    @State("Product with ID 1 exists")
    public void productWithId1Exists() {
        providerStub.addProduct(1, "Laptop", 999.99, "Electronics", true);
        providerStub.addProductDetails(1, "TechCorp", "2 years", 50);
    }
}

