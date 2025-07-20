import { afterEach, describe, expect, it } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import { IssueReportInterface } from "../../../../types/IssueReportInterface";
import {fetchAssignedIssues} from "../../../../services/authorized/FetchAssignedIssues";

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



describe("fetchAssignedIssues after login", () => {
    afterEach(() => {
        mock.reset();
    });

    it("logs in and successfully fetches assigned issues", async () => {
        const email = "tech@example.com";
        const password = "techpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const limit = 5;
        const offset = 0;

        const mockedIssues: IssueReportInterface[] = [
            {
                id: 1,
                description: "Broken projector",
                userId: 2,
                room: { id: 101, name: "Room A", capacity: 40 },
                assignedTo: 5,
            },
            {
                id: 2,
                description: "No lights",
                userId: 3,
                room: { id: 102, name: "Room B", capacity: 30 },
            },
        ];

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResponse = await requestLogin(email, password, deviceType);
        expect(loginResponse).toBe(true);

        // Mock issue fetch
        mock
            .onGet(`issue-reports/me?limit=${limit}&offset=${offset}`)
            .reply(200, { data: mockedIssues });

        const result = await fetchAssignedIssues(limit, offset);
        expect(result).toEqual(mockedIssues);
    });

    it("logs in but fails to fetch assigned issues", async () => {
        const email = "failuser@example.com";
        const password = "failpass";
        const deviceType = "WEB";

        const limit = 10;
        const offset = 5;

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Mock failure
        mock
            .onGet(`issue-reports/me?limit=${limit}&offset=${offset}`)
            .networkError();

        await expect(fetchAssignedIssues(limit, offset)).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });
    });
});
