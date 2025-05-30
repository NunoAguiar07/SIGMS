
/**
 * Interface "Link" represent the information of the Link.
 * @param rel represent the information of the link.
 * @param href represent the url of the link.
 * @param type represents the type of the link.
 * @param title represent the title for that link.
 */
export interface Link {
    rel: string;
    href: string;
    type?: string | null;
    title?: string | null;
}