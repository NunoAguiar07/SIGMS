import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { apiUrl } from "../../../../services/fetchWelcome";
import { SubjectInterface } from "../../../../types/SubjectInterface";
import {requestLogin} from "../../../../services/auth/requestLogin";
import {fetchSubjects} from "../../../../services/authorized/FetchSubjects";

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

describe("fetchSubjects after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and fetches subjects without search query", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const mockedSubjects: SubjectInterface[] = [
            { id: 1, name: "Mathematics" },
            { id: 2, name: "Physics" },
        ];

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        mock.onGet("subjects").reply(200, { data: mockedSubjects });

        const result = await fetchSubjects("");
        expect(result).toEqual(mockedSubjects);
    });

    it("logs in and fetches subjects with search query", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const searchQuery = "Math";

        const mockedSubjects: SubjectInterface[] = [
            { id: 1, name: "Mathematics" },
        ];

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        await requestLogin(email, password, deviceType);

        mock
            .onGet(`subjects?search=${encodeURIComponent(searchQuery)}`)
            .reply(200, { data: mockedSubjects });

        const result = await fetchSubjects(searchQuery);
        expect(result).toEqual(mockedSubjects);
    });

    it("logs in but fetch fails with network error", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        mock.onGet("subjects").networkError();

        await expect(fetchSubjects("")).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });

    it("returns empty array if response status is not 200", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        await requestLogin(email, password, deviceType);

        mock.onGet("subjects").reply(204);

        const result = await fetchSubjects("");
        expect(result).toEqual([]);
    });
});
