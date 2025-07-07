import {useCallback, useState} from "react";
import {IssueReportInterface} from "../types/IssueReportInterface";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {Alert} from "react-native";
import {useFocusEffect} from "expo-router";
import {fetchUnassignedIssues} from "../services/authorized/FetchUnassignedIssues";
import {requestAssignTechnicianToIssue} from "../services/authorized/RequestAssignTechnicianToIssue";


export const useUnassignedIssues = () => {
    const [issues, setIssues] = useState<IssueReportInterface[]>([]);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [hasNext, setHasNext] = useState<boolean>(false);
    const LIMIT = 9;
    const TIMING = 150000; // 2.5min

    const loadIssues = async () => {
        setLoading(true);
        try {
            const offset = currentPage * LIMIT;
            const data = await fetchUnassignedIssues(LIMIT, offset);
            setIssues(data);
            setHasNext(data.length === LIMIT);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setLoading(false);
        }
    };

    const assignIssue = async (issueId: number) => {
        try {
            const success = await requestAssignTechnicianToIssue(issueId);
            if (success) {
                Alert.alert("Assigned", "You have been assigned to the issue.");
                loadIssues();
            } else {
                Alert.alert("Error", "Failed to assign the issue.");
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert("Error", "Failed to assign the issue.");
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadIssues();
            const interval = setInterval(() => {
                loadIssues();
            }, TIMING);
            return () => clearInterval(interval);
        }, [currentPage])
    );

    const handleNext = () => setCurrentPage(prev => prev + 1);
    const handlePrevious = () => setCurrentPage(prev => Math.max(0, prev - 1));

    return {
        issues,
        error,
        loading,
        currentPage,
        hasNext,
        assignIssue,
        goToNextPage: handleNext,
        goToPreviousPage: handlePrevious,
    };
};