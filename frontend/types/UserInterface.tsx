export interface UserInterface {
    user: {
        id: number;
        username: string;
        email: string;
        image: number[];
        university: string;
    },
    office? : {
        room :{
            id: number;
            name: string;
            capacity: number;
        }
    }
}