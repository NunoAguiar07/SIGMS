import {useCallback, useState} from "react";
import {IssueReportInterface} from "../../interfaces/IssueReportInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {GetIssuesRequest} from "../../requests/authorized/GetIssueReports";
import {AssignTechnicianToIssueRequest} from "../../requests/authorized/AssignTechnicianToIssue";
import {Alert} from "react-native";
import ErrorHandler from "../(public)/error";
import TechnicianUnassignedIssuesScreen from "../../screens/UnassignedIssuesScreen";
import {useFocusEffect} from "expo-router";


const TechnicianUnassignedIssues = () => {
    const [issues, setIssues] = useState<IssueReportInterface[]>([]);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [page, setPage] = useState(0);
    const limit = 10;

    const loadIssues = () => {
        setIsLoading(true);
        const fetch = GetIssuesRequest(limit, page * limit, true, setIssues, setError);
        fetch().finally(() => setIsLoading(false));
    };

    useFocusEffect(
        useCallback(() => {
            loadIssues();
        }, [page])
    )

    const handleAssign = async (issueId: number) => {
        const assign = AssignTechnicianToIssueRequest(issueId, setError);
        const response = await assign();
        if (response) {
            Alert.alert('Assigned', 'You have been assigned to the issue.');
            loadIssues();
        } else {
            Alert.alert('Error', 'Failed to assign the issue.');
        }
    };

    const handleNext = () => setPage((prev) => prev + 1);
    const handlePrevious = () => setPage((prev) => Math.max(prev - 1, 0));

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <TechnicianUnassignedIssuesScreen
            issues={issues}
            isLoading={isLoading}
            currentPage={page}
            onNext={handleNext}
            onPrevious={handlePrevious}
            onAssign={handleAssign}
            hasNext={issues.length === limit}
        />
    );
};

export default TechnicianUnassignedIssues;