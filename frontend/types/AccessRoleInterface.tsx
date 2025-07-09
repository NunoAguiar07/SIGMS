
export interface AccessRoleInterface {
    id: number;
    user: {
        username: string;
        email: string;
    };
    verificationToken: string;
    requestedRole: string;
    createdAt: string;
}