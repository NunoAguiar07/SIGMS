import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { SchoolClassInterface } from "../../../../types/SchoolClassInterface";
import {fetchSubjectClasses} from "../../../../services/authorized/FetchSubjectClasses";

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

describe("fetchSubjectClasses after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches classes for a subject", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const subjectId = 123;

        const mockedClasses: SchoolClassInterface[] = [
            {
                id: 1,
                name: "Class A",
                subject: {
                    id: subjectId,
                    name: "Mathematics",
                },
            },
            {
                id: 2,
                name: "Class B",
                subject: {
                    id: subjectId,
                    name: "Mathematics",
                },
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock fetch
        mock
            .onGet(`/subjects/${encodeURIComponent(subjectId)}/classes`)
            .reply(200, { data: mockedClasses });

        const result = await fetchSubjectClasses(subjectId);
        expect(result).toEqual(mockedClasses);
    });

    it("logs in but fails to fetch classes", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const subjectId = 456;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate network error
        mock
            .onGet(`/subjects/${encodeURIComponent(subjectId)}/classes`)
            .networkError();

        await expect(fetchSubjectClasses(subjectId)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
