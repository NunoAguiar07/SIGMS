import api from "../interceptors/DeviceInterceptor";
import {handleAxiosError} from "../../Utils/HandleAxiosError";
import {IssueReportInterface} from "../../interfaces/IssueReportInterface";



export const GetIssuesByRoomRequest = async (
    roomId: number,
    setIssues: (issues: IssueReportInterface[]) => void,
    setError: (error: any) => void
) => {
    try {
        const response = await api.get(`/rooms/${encodeURIComponent(roomId)}/issues?limit=100&offset=0`);
        if (response.status === 200) {
            setIssues(response.data); // or response.data.data depending on your API response structure
        }
        console.log(response.data)
    } catch (error) {
        handleAxiosError(error, setError);
    }
};
