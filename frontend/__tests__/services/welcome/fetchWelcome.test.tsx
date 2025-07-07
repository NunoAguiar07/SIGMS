import axios from "axios";
import MockAdapter from "axios-mock-adapter";
import { fetchWelcome, apiUrl } from "../../../services/fetchWelcome";
import { WelcomeData } from "../../../types/welcome/WelcomeInterfaces";
import {afterEach, describe, expect, it} from "@jest/globals";

const mock = new MockAdapter(axios);


describe("fetchWelcome", () => {
    afterEach(() => {
        mock.reset();
    });

    it("returns welcome data successfully", async () => {
        const mockData: WelcomeData = {
            title: "Welcome to SIGMS",
        };

        mock.onGet(apiUrl).reply(200, mockData);

        const result = await fetchWelcome();
        expect(result).toEqual(mockData);
    });

    it("throws an error when the API request fails", async () => {
        mock.onGet(apiUrl).networkError();
        await expect(fetchWelcome()).rejects.toMatchObject({
            status: 500,
            message: "Network Error"
        });
    });
});


