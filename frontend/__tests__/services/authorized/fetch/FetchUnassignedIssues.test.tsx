import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { IssueReportInterface } from "../../../../types/IssueReportInterface";
import {fetchUnassignedIssues} from "../../../../services/authorized/FetchUnassignedIssues";

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

describe("fetchUnassignedIssues after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches unassigned issues", async () => {
        const email = "user@example.com";
        const password = "userpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";
        const limit = 10;
        const offset = 0;

        const mockedIssues: IssueReportInterface[] = [
            {
                id: 1,
                room: { id: 101, name: "Room A" },
                userId: 201,
                description: "Broken projector",
                assignedTo: undefined,
            },
            {
                id: 2,
                room: { id: 102, name: "Room B" },
                userId: 202,
                description: "Air conditioning not working",
                assignedTo: undefined,
            },
        ];

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        mock
            .onGet(`/issue-reports?limit=${limit}&offset=${offset}&unassigned=true`)
            .reply(200, { data: mockedIssues });

        const result = await fetchUnassignedIssues(limit, offset);
        expect(result).toEqual(mockedIssues);
    });

    it("logs in but fails to fetch unassigned issues", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";
        const limit = 10;
        const offset = 0;

        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        mock
            .onGet(`/issue-reports?limit=${limit}&offset=${offset}&unassigned=true`)
            .networkError();

        await expect(fetchUnassignedIssues(limit, offset)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
