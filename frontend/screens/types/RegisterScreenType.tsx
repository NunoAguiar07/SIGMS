import {UniversityInterface} from "../../types/UniversityInterface";


export interface RegisterScreenType {
    email: string;
    username: string;
    password: string;
    role: string;
    universities: UniversityInterface[];
    searchQuery: string;
    onEmailChange: (text: string) => void;
    onUsernameChange: (text: string) => void;
    onPasswordChange: (text: string) => void;
    onRoleChange: (itemValue: string) => void;
    onSearchChange: (text: string) => void;
    onUniversitySelect: (university: UniversityInterface) => void;
    onRegister: () => void;
    onNavigateToLogin: () => void;
}
