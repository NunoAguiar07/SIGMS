import {UserInterface} from "../../types/UserInterface";

export interface SearchUsersScreenType {
    users: UserInterface[];
    searchQuery: string;
    onSearchChange: (query: string) => void;
}