import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {CreateIssueReportRequest} from "../../../../services/authorized/CreateIssueReport";

// Mock SecureStore (used by the login flow/interceptor)
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

describe("CreateIssueReportRequest after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and creates an issue report successfully", async () => {
        const email = "test@example.com";
        const password = "password123";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const roomId = 123;
        const description = "Air conditioner not working";

        // Mock login response
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });

        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock issue report creation
        mock.onPost(`rooms/${roomId}/issues/add`, { description }).reply(201);

        const result = await CreateIssueReportRequest(roomId, description);
        expect(result).toBe(true);
    });

    it("logs in but fails to create an issue report", async () => {
        const email = "test2@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const roomId = 456;
        const description = "Lights are flickering";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Mock issue report failure
        mock.onPost(`rooms/${roomId}/issues/add`, { description }).networkError();

        await expect(
            CreateIssueReportRequest(roomId, description)
        ).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
