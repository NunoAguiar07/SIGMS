import {handleAxiosError} from "../../Utils/HandleAxiosError";
import api from "../interceptors/DeviceInterceptor";

export const CreateIssueReportRequest = (
    roomId: number,
    description: string,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.post(
                `rooms/${encodeURIComponent(roomId)}/issues/add`,
                { description },
                { withCredentials: true }
            );

            if (response.status === 201) {
                console.log('Issue created:', response.data);
                return true;
            }
        } catch (error) {
            handleAxiosError(error, setError);
        }
    };
};