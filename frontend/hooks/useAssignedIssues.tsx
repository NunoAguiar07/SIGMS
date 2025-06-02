import {Alert} from "react-native";
import {useCallback, useState} from "react";
import {IssueReportInterface} from "../types/IssueReportInterface";
import {useFocusEffect} from "expo-router";
import {fetchAssignedIssues} from "../services/authorized/fetchAssignedIssues";
import {requestUpdateIssue} from "../services/authorized/requestUpdateIssue";
import {requestFixIssue} from "../services/authorized/requestFixIssue";
import {requestUnassignFromIssue} from "../services/authorized/requestUnassignFromIssue";
import {ParsedError} from "../types/errors/ParseErrorTypes";


export const useAssignedIssues = () => {
    const [issues, setIssues] = useState<IssueReportInterface[]>([]);
    const [error, setError] = useState<ParsedError | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [page, setPage] = useState(0);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editedDescription, setEditedDescription] = useState('');
    const limit = 9;
    const TIMING = 150000; // 2.5min

    const loadIssues = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const data = await fetchAssignedIssues(limit, page * limit);
            setIssues(data);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    }, [page]);

    useFocusEffect(
        useCallback(() => {
            loadIssues();
            const interval = setInterval(() => {
                loadIssues();
            }, TIMING);
            return () => clearInterval(interval);
        }, [loadIssues])
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
        setIsLoading(true);
        try {
            const success = await requestUpdateIssue(issueId, editedDescription);
            if (success) {
                Alert.alert('Updated', 'The issue description has been updated.');
                setEditingId(null);
                await loadIssues();
            } else {
                Alert.alert('Error', 'Failed to update the issue.');
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert('Error', 'Failed to update the issue.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleFix = async (issueId: number) => {
        setIsLoading(true);
        try {
            const success = await requestFixIssue(issueId);
            if (success) {
                Alert.alert('Fixed', 'The issue has been marked as fixed.');
                await loadIssues();
            } else {
                Alert.alert('Error', 'Failed to mark the issue as fixed.');
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert('Error', 'Failed to mark the issue as fixed.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleUnassigned = async (issueId: number) => {
        setIsLoading(true);
        try {
            const success = await requestUnassignFromIssue(issueId);
            if (success) {
                Alert.alert('Success', 'Left the issue correctly');
                await loadIssues();
            } else {
                Alert.alert('Error', 'Failed to unassign from the issue.');
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert('Error', 'Failed to unassign from the issue.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleNext = () => setPage((prev) => prev + 1);
    const handlePrevious = () => setPage((prev) => Math.max(prev - 1, 0));

    return {
        issues,
        error,
        isLoading,
        page,
        editingId,
        editedDescription,
        setEditedDescription,
        hasNext: issues.length === limit,
        handleStartEditing,
        handleCancelEditing,
        handleUpdate,
        handleFix,
        handleUnassigned,
        handleNext,
        handlePrevious,
        loadIssues
    };
};