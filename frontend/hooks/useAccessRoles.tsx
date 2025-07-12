import {useCallback, useState} from "react";
import {AccessRoleInterface} from "../types/AccessRoleInterface";
import {fetchPendingApprovals} from "../services/authorized/FetchPendingApprovals";
import {handleAxiosError} from "../utils/HandleAxiosError";
import {useFocusEffect} from "expo-router";
import {requestProcessApproval} from "../services/authorized/RequestProcessApproval";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {useAlert} from "./notifications/useAlert";



export const useAccessRoles = () => {
    const [approvals, setApprovals] = useState<AccessRoleInterface[]>([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const LIMIT = 7;
    const TIMING = 150000; // 2.5min

    const showAlert = useAlert()

    const fetchApprovals = async (page: number) => {
        setIsLoading(true);
        setError(null);
        const offset = page * LIMIT;
        try {
            const data = await fetchPendingApprovals(LIMIT, offset);
            setApprovals(data);
            setHasNext(data.length === LIMIT);
        } catch (err) {
            setError(handleAxiosError(err));
        } finally {
            setIsLoading(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            fetchApprovals(currentPage);
            const interval = setInterval(() => {
                fetchApprovals(currentPage);
            }, TIMING);

            return () => clearInterval(interval);
        }, [currentPage])
    );

    const handleApprove = async (id: number) => {
        const approval = approvals.find(a => a.id === id);
        if (!approval) return;

        try {
            const success = await requestProcessApproval(approval.verificationToken, true);
            if (success) {
                showAlert('Success', 'User has been approved');
                fetchApprovals(currentPage);
            }
        } catch (err) {
            setError(handleAxiosError(err));
        }
    };

    const handleReject = async (id: number) => {
        const approval = approvals.find(a => a.id === id);
        if (!approval) return;
 
        try {
            const success = await requestProcessApproval(approval.verificationToken, false);
            if (success) {
                showAlert('Success', 'User has been rejected');
                fetchApprovals(currentPage);
            }
        } catch (err) {
            setError(handleAxiosError(err));
        }
    };

    const handleNext = () => setCurrentPage(prev => prev + 1);
    const handlePrevious = () => setCurrentPage(prev => Math.max(0, prev - 1));

    return {
        approvals,
        currentPage,
        hasNext,
        isLoading,
        error,
        handleApprove,
        handleReject,
        handleNext,
        handlePrevious
    };
};