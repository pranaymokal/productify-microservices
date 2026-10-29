export interface Product {
    productId: number;
    productName: string;
    productPrice: number;
    productDescription: string;
    stock: number;
    category: string;
    selected?: boolean; // Optional property to track selection
}