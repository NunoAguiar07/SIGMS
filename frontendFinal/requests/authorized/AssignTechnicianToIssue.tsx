import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";


export const AssignTechnicianToIssueRequest = (
    issueId: number,
    setError: (error: any) => void
) => {
    return async () => {
        try {
            const response = await api.put(
                `issue-reports/${encodeURIComponent(issueId)}/assign`,
                {},
                { withCredentials: true }
            );

            if (response.status === 200) {
                console.log('Technician assigned:', response.data);
                return response.data;
            }
        } catch (error) {
            handleAxiosError(error, setError);
        }
    };
};