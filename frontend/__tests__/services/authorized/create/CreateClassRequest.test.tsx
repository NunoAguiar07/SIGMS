import { afterEach, describe, expect, it } from "@jest/globals";
import { createClass } from "../../../../services/authorized/CreateClassRequest";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";


// Mocks
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

describe("createClass after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and creates a class successfully", async () => {
        const email = "test@example.com";
        const password = "password123";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const subjectId = 42;
        const className = "New Class";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        mock.onPost(`subjects/${subjectId}/classes/add`, { name: className }).reply(201);

        const createResult = await createClass(className, subjectId);
        expect(createResult).toBe(true);
    });



    it("fails class creation after login", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const subjectId = 50;
        const className = "Fail Class";


        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });

        await requestLogin(email, password, deviceType);

        mock.onPost(`subjects/${subjectId}/classes/add`, { name: className }).networkError();

        await expect(createClass(className, subjectId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });

});
