import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { AccessRoleInterface } from "../../../../types/AccessRoleInterface";
import {fetchPendingApprovals} from "../../../../services/authorized/FetchPendingApprovals";

// Mock SecureStore for login flow
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

describe("fetchPendingApprovals after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches pending approvals", async () => {
        const email = "admin@example.com";
        const password = "adminpass";
        const deviceType = "web";
        const token = "fake-jwt-token";

        const limit = 10;
        const offset = 0;

        const mockedApprovals: AccessRoleInterface[] = [
            {
                id: 1,
                user: {
                    name: "Alice Admin",
                    email: "alice@example.com",
                },
                verificationToken: "token123",
                requestedRole: "ADMIN",
                createdAt: "2025-07-05T10:00:00Z",
            },
            {
                id: 2,
                user: {
                    name: "Bob User",
                    email: "bob@example.com",
                },
                verificationToken: "token456",
                requestedRole: "USER",
                createdAt: "2025-07-06T12:00:00Z",
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse.success).toBe(true);

        // Mock fetch pending approvals
        mock
            .onGet(`assess-roles?limit=${limit}&offset=${offset}`)
            .reply(200, { data: mockedApprovals });

        const result = await fetchPendingApprovals(limit, offset);
        expect(result).toEqual(mockedApprovals);
    });

    it("logs in but fails to fetch pending approvals", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "web";

        const limit = 5;
        const offset = 10;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock
            .onGet(`assess-roles?limit=${limit}&offset=${offset}`)
            .networkError();

        await expect(fetchPendingApprovals(limit, offset)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
