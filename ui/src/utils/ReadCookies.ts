import {type User} from "@/types/users/User.ts";

const CURRENT_USER_COOKIE = "current-user";

export function readUserFromCookie(): User | null {
    try {
        const cookies = document.cookie;

        if (!cookies) {
            console.warn('No cookies found');
            return null;
        }

        const pattern = new RegExp(`(^|;)\\s*${CURRENT_USER_COOKIE}\\s*=\\s*([^;]+)`);
        const match = pattern.exec(cookies);
        if (!match) {
            console.warn(`Cookie '${CURRENT_USER_COOKIE}' not found`);
            return null;
        }

        const userJsonBase64 = match[2];
        if (!userJsonBase64) {
            console.warn(`Cookie '${CURRENT_USER_COOKIE}' has no value`);
            return null;
        }

        const userJson = atob(userJsonBase64);
        return JSON.parse(userJson) as User;

    } catch (error) {
        console.error('Error reading user from cookie:', error);
        return null;
    }
}