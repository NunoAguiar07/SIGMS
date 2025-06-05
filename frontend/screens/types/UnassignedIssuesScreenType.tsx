import {IssueReportInterface} from "../../types/IssueReportInterface";


export interface UnassignedIssuesScreenType {
    issues: IssueReportInterface[];
    onAssign: (issueId: number) => void;
    currentPage: number;
    onNext: () => void;
    onPrevious: () => void;
    hasNext: boolean;
}