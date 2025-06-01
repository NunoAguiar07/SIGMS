import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";


export const CreateSubjectRequest = (subjectName: string, setError: (err: any) => void) => {
    return async () => {
        try {
            const res = await api.post('/subjects/add',
                { name : subjectName },
                { withCredentials: true }
            );
            console.log(res);
            return res.status === 201;
        } catch (err) {
            handleAxiosError(err, setError)
        }
    };
};