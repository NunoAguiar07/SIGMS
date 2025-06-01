import {useCallback, useState} from "react";
import {IssueReportInterface} from "../../../interfaces/IssueReportInterface";
import {ErrorInterface} from "../../../interfaces/ErrorInterface";
import {GetAssignedIssuesRequest} from "../../../requests/authorized/GetAssignedIssueRequest";
import {useFocusEffect} from "expo-router";
import {FixIssueReportRequest} from "../../../requests/authorized/FixIssueReport";
import {Alert} from "react-native";
import {UpdateIssueReportRequest} from "../../../requests/authorized/UpdateIssueReport";
import {UnassignTechnicianToIssueRequest} from "../../../requests/authorized/UnassignTechnicianToIssue";
import ErrorHandler from "../../(public)/error";
import {TechnicianAssignedIssuesScreen} from "../../../screens/TechnicianAssignedIssuesScreen";


const TechnicianAssignedIssues = () => {
    const [issues, setIssues] = useState<IssueReportInterface[]>([]);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [page, setPage] = useState(0);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editedDescription, setEditedDescription] = useState('');
    const limit = 10;

    const loadIssues = () => {
        setIsLoading(true);
        const fetch = GetAssignedIssuesRequest(limit, page * limit, setIssues, setError);
        fetch().finally(() => setIsLoading(false));
    };

    useFocusEffect(
        useCallback(() => {
            loadIssues();
        }, [page])
    );

    const handleStartEditing = (issue: IssueReportInterface) => {
        setEditingId(issue.id);
        setEditedDescription(issue.description || '');
    };

    const handleCancelEditing = () => {
        setEditingId(null);
        setEditedDescription('');
    };

    const handleUpdate = async (issueId: number) => {
        const update = UpdateIssueReportRequest(issueId, editedDescription, setError);
        const response = await update();
        if (response) {
            Alert.alert('Updated', 'The issue description has been updated.');
            setEditingId(null);
            loadIssues();
        } else {
            Alert.alert('Error', 'Failed to update the issue.');
        }
    };

    const handleFix = async (issueId: number) => {
        const fix = FixIssueReportRequest(issueId, setError);
        const response = await fix();
        if (response) {
            Alert.alert('Fixed', 'The issue has been marked as fixed.');
            loadIssues();
        } else {
            Alert.alert('Error', 'Failed to mark the issue as fixed.');
        }
    };

    const handleUnassigned = async (issueId: number) => {
        console.log('Unassigning issue with ID:', issueId);
        const unassign = UnassignTechnicianToIssueRequest(issueId, setError);
        const response = await unassign();
        if (response) {
            Alert.alert('Left the issue correctly');
            loadIssues();
        } else {
            Alert.alert('Error', 'Failed to mark the issue as fixed.');
        }
    };

    const handleNext = () => setPage((prev) => prev + 1);
    const handlePrevious = () => setPage((prev) => Math.max(prev - 1, 0));

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    return (
        <TechnicianAssignedIssuesScreen
            issues={issues}
            isLoading={isLoading}
            currentPage={page}
            onNext={handleNext}
            onPrevious={handlePrevious}
            onFix={handleFix}
            onEdit={handleStartEditing}
            onUpdate={handleUpdate}
            onUnassigned={handleUnassigned}
            onCancelEdit={handleCancelEditing}
            editingId={editingId}
            editedDescription={editedDescription}
            setEditedDescription={setEditedDescription}
            hasNext={issues.length === limit}
        />
    );
};

export default TechnicianAssignedIssues;