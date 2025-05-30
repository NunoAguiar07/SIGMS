
export interface AccessRoleInterface {
    id: number;
    user: {
        name: string;
        email: string;
    };
    verificationToken: string;
    requestedRole: string;
    createdAt: string;
}