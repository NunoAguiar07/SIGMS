import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { SchoolClassInterface } from "../../../../types/SchoolClassInterface";
import {fetchUserClasses} from "../../../../services/authorized/FetchUserClasses";

jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("fetchUserClasses after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches user's classes", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const mockedClasses: SchoolClassInterface[] = [
            {
                id: 1,
                name: "Class A",
                subject: {
                    id: 10,
                    name: "Mathematics",
                },
            },
            {
                id: 2,
                name: "Class B",
                subject: {
                    id: 20,
                    name: "Physics",
                },
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock fetchUserClasses
        mock.onGet("/userClasses").reply(200, { data: mockedClasses });

        const result = await fetchUserClasses();
        expect(result).toEqual(mockedClasses);
    });

    it("logs in but fails to fetch user's classes", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate fetch failure
        mock.onGet("/userClasses").networkError();

        await expect(fetchUserClasses()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
