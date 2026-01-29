export interface Transaction {
    id: number;
    userId: string;
    type: "VOID" | "SALE";
    status: "APPROVED" | "DECLINED" | "PENDING" | "VOIDED";
    currencyCode: string;
    createdAt: string;
    amount: number;
}