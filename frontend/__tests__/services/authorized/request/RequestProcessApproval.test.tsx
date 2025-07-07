import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {requestProcessApproval} from "../../../../services/authorized/RequestProcessApproval";

// Mock SecureStore for login
jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

// Mock error handler
jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("requestProcessApproval after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully approves a process (204)", async () => {
        const email = "approver@example.com";
        const password = "securePass";
        const deviceType = "web";
        const token = "approval-token-123";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult.success).toBe(true);

        // Mock approval request
        mock
            .onPut(`assess-roles/validate?token=${token}&status=APPROVED`)
            .reply(204);

        const result = await requestProcessApproval(token, true);
        expect(result).toBe(true);
    });

    it("logs in and successfully rejects a process (204)", async () => {
        const token = "rejection-token-456";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`assess-roles/validate?token=${token}&status=REJECTED`)
            .reply(204);

        const result = await requestProcessApproval(token, false);
        expect(result).toBe(true);
    });

    it("returns false if status is not 204", async () => {
        const token = "invalid-token";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`assess-roles/validate?token=${token}&status=APPROVED`)
            .reply(200); // wrong status

        const result = await requestProcessApproval(token, true);
        expect(result).toBe(false);
    });

    it("throws parsed error on network failure", async () => {
        const token = "network-fail-token";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPut(`assess-roles/validate?token=${token}&status=APPROVED`)
            .networkError();

        await expect(requestProcessApproval(token, true)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
