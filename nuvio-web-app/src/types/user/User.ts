export interface User {
    id: string;
    email: string;
    phoneNumber: string;
    firstName: string;
    lastName: string;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
    lastLoginAt?: string;
    profilePictureUrl?: string;
    gender?: string;
    roles: {
        id: string;
        name: string;
    }[];
}