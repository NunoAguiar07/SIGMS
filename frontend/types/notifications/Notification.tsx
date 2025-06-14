import {NavigateToUrl} from "./NavigateToUrl";

export interface Notification {
    id?: number;
    title: string;
    message: string;
    data: NavigateToUrl;
}

