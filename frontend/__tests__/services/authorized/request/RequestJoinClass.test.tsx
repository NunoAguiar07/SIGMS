import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import {joinClass} from "../../../../services/authorized/RequestJoinClass";

// Mock SecureStore
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

describe("joinClass after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and successfully joins a class (204)", async () => {
        const email = "student@example.com";
        const password = "mypassword";
        const deviceType = "web";
        const token = "fake-jwt-token";
        const subjectId = 10;
        const classId = 20;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const login = await requestLogin(email, password, deviceType);
        expect(login.success).toBe(true);

        // Mock successful join
        mock
            .onPost(`/subjects/${subjectId}/classes/${classId}/users/add`)
            .reply(204);

        const result = await joinClass(subjectId, classId);
        expect(result).toBe(true);
    });

    it("returns false if join response is not 204", async () => {
        const subjectId = 10;
        const classId = 21;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPost(`/subjects/${subjectId}/classes/${classId}/users/add`)
            .reply(200); // wrong status code

        const result = await joinClass(subjectId, classId);
        expect(result).toBe(false);
    });

    it("throws parsed error on network failure", async () => {
        const subjectId = 10;
        const classId = 22;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin("user@example.com", "pass", "web");

        mock
            .onPost(`/subjects/${subjectId}/classes/${classId}/users/add`)
            .networkError();

        await expect(joinClass(subjectId, classId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
