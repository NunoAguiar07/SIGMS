import { afterEach, describe, expect, it, jest } from "@jest/globals";
import MockAdapter from "axios-mock-adapter";
import api from "../../../../services/interceptors/DeviceInterceptor";
import { requestLogin } from "../../../../services/auth/requestLogin";
import { apiUrl } from "../../../../services/fetchWelcome";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { UserInfo } from "../../../../types/auth/authTypes";
import {fetchUserInfo} from "../../../../services/authorized/FetchUserInfo";

// Mock SecureStore
jest.mock("expo-secure-store", () => ({
    getItemAsync: jest.fn(() => Promise.resolve("fake-jwt-token")),
    setItemAsync: jest.fn(() => Promise.resolve()),
}));

// Mock AsyncStorage
jest.mock("@react-native-async-storage/async-storage", () => ({
    multiSet: jest.fn(() => Promise.resolve()),
}));

// Mock error handler
jest.mock("../../../../utils/HandleAxiosError", () => ({
    handleAxiosError: jest.fn((error: any) => ({
        status: error.response?.status || 500,
        message: error.response?.data?.detail || error.message || error,
    })),
}));

const mock = new MockAdapter(api);

describe("fetchUserInfo after login", () => {
    afterEach(() => {
        mock.reset();
        jest.clearAllMocks();
    });

    it("logs in and fetches user info successfully", async () => {
        const email = "test@example.com";
        const password = "testpass";
        const deviceType = "WEB";
        const token = "fake-jwt-token";

        const mockedUserInfo: UserInfo = {
            userId: "1",
            universityId: "2",
            userRole: "ADMIN",
        };

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token });
        const loginResult = await requestLogin(email, password, deviceType);
        expect(loginResult).toBe(true);

        // Mock user info fetch
        mock.onGet("userInfo").reply(200, mockedUserInfo);

        const result = await fetchUserInfo();

        expect(result).toEqual(mockedUserInfo);
        expect(AsyncStorage.multiSet).toHaveBeenCalledWith([
            ["userId", JSON.stringify(mockedUserInfo.userId)],
            ["universityId", JSON.stringify(mockedUserInfo.universityId)],
            ["userRole", mockedUserInfo.userRole],
        ]);
    });

    it("fails to fetch user info and throws parsed error", async () => {
        const email = "fail@example.com";
        const password = "wrongpass";
        const deviceType = "WEB";

        // Mock login
        mock.onPost(`${apiUrl}auth/login`).reply(200, { token: "fake-jwt-token" });
        await requestLogin(email, password, deviceType);

        // Simulate error
        mock.onGet("userInfo").networkError();

        await expect(fetchUserInfo()).rejects.toMatchObject({
            status: 500,
            message: "Network Error",
        });

        expect(AsyncStorage.multiSet).not.toHaveBeenCalled();
    });
});
