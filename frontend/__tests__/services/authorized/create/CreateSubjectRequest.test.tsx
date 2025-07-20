import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {createSubject} from "../../../../services/authorized/CreateSubjectRequest";

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

describe("createSubject after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully creates a subject", async () => {
        const email = "prof@example.com";
        const password = "pass123";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const subjectName = "Mathematics";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });

        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock subject creation success
        mock.onPost("/subjects/add", { name: subjectName }).reply(201);

        const result = await createSubject(subjectName);
        expect(result).toBe(true);
    });

    it("logs in but fails to create a subject", async () => {
        const email = "prof2@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const subjectName = "Physics";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Mock subject creation failure
        mock.onPost("/subjects/add", { name: subjectName }).networkError();

        await expect(createSubject(subjectName)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
