import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const CreateRoomRequest = (
    roomName: string,
    capacity: number,
    roomType: 'STUDY' | 'CLASS' | 'OFFICE',
    setError: (err: any) => void
) => {
    return async () => {
        try {
            const res = await api.post('/rooms/add', {
                name: roomName,
                capacity: capacity,
                type: roomType
            },
                { withCredentials: true });
            return res.status === 201;
        } catch (err) {
            handleAxiosError(err, setError)
        }
    };
};