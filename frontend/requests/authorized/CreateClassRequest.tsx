import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const CreateClassRequest = (className: string, subjectId: number, setError: (err: any) => void) => {
    return async () => {
        try {
            const res = await api.post(`subjects/${encodeURIComponent(subjectId)}/classes/add`,
                { name: className},
            { withCredentials: true }
        );
            return res.status === 201;
        } catch (err) {
            handleAxiosError(err, setError);
        }
    };
};