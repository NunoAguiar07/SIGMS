export interface UserInfo {
    userId: string;
    universityId: string;
    userRole: string;
}

export interface AuthError {
    status?: number;
    message: string;
}