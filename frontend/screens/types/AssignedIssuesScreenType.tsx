import {IssueReportInterface} from "../../types/IssueReportInterface";

export interface AssignedIssuesScreenType {
    issues: IssueReportInterface[];
    currentPage: number;
    hasNext: boolean;
    onNext: () => void;
    onPrevious: () => void;
    onFix: (issueId: number) => Promise<void>;
    onEdit: (issue: IssueReportInterface) => void;
    onUpdate: (issueId: number) => Promise<void>;
    onUnassigned: (issueId: number) => Promise<void>;
    onCancelEdit: () => void;
    editingId: number | null;
    editedDescription: string;
    setEditedDescription: (text: string) => void;
}