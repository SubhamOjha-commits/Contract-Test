package com.sdet.pact.provider.verification;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.sdet.pact.provider.stubs.UserProviderStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Provider Verification Tests for User Service
 * 
 * This test verifies that the provider implementation satisfies
 * the contracts defined by consumers.
 * 
 * Key Features:
 * - Verifies contracts from PACT files
 * - Uses state handlers to setup test data
 * - Runs against a stub/mock provider service
 */
@Provider("UserProvider")
@PactFolder("../consumer-service/target/pacts")
@DisplayName("User Provider Verification Tests")
public class UserProviderPactTest {

    private UserProviderStub providerStub;
    private static final int PROVIDER_PORT = 8080;

    @BeforeEach
    void setup(PactVerificationContext context) {
        // Start the stub provider service
        providerStub = new UserProviderStub(PROVIDER_PORT);
        providerStub.start();
        
        // Configure the test target
        context.setTarget(new HttpTestTarget("localhost", PROVIDER_PORT));
    }

    @AfterEach
    void tearDown() {
        if (providerStub != null) {
            providerStub.stop();
            try {
                // Small delay to ensure port is released
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * This is the main verification method that runs for each interaction
     * defined in the PACT files
     */
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    @DisplayName("Verify PACT interactions")
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * State Handlers - Setup test data for specific provider states
     */

    @State("User with ID 1 exists")
    public void userWithId1Exists() {
        // Setup: Ensure user with ID 1 exists in the stub
        providerStub.addUser(1, "John Doe", "john.doe@example.com", "ADMIN", true);
    }

    @State("User with ID 999 does not exist")
    public void userWithId999DoesNotExist() {
        // Setup: Ensure user with ID 999 does NOT exist
        providerStub.removeUser(999);
    }

    @State("User creation is allowed")
    public void userCreationIsAllowed() {
        // Setup: Configure stub to allow user creation
        providerStub.enableUserCreation();
    }

    /**
     * Additional state handlers for update and delete operations
     */

    @State("User with ID 1 exists for update")
    public void userExistsForUpdate() {
        providerStub.addUser(1, "John Doe", "john.doe@example.com", "ADMIN", true);
    }

    @State("User with ID 1 exists for deletion")
    public void userExistsForDeletion() {
        providerStub.addUser(1, "John Doe", "john.doe@example.com", "ADMIN", true);
    }
}

