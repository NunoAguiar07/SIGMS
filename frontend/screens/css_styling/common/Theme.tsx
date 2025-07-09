
export const theme = {
    colors: {
        primary: '#671b22',
        primaryDark: '#651920',
        primaryLight: '#651c24',
        secondary: '#bf9e9b',
        background: {
            primary: '#ead6bd',
            secondary: '#bf9e9b',
            tertiary: '#caa8a0',
            onPrimary: '#f8f0e6',
            card: '#bb9996',
            transparent: '#00000000',
            white: '#ffffff',
            cream: '#f4ece5',
        },
        text: {
            primary: '#671b22',
            secondary: '#333',
            black: '#000',
            light: '#666',
            white: '#fff',
            cream: '#f4ece5',
        },
        status: {
            success: '#4CAF50',
            error: '#F44336',
            warning: '#FFC107',
        },
        border: '#651c24',
        shadow: '#000',
    },
    fonts: {
        family: {
            regular: 'RobotoCondensed-Regular',
            light: 'RobotoCondensed-Light',
            bold: 'RobotoCondensed-Bold',
            black: 'RobotoCondensed-Black',
        },
        sizes: {
            small: 14,
            medium: 16,
            large: 18,
            xl: 20,
            xxl: 24,
            h1: 36,
        },
        weights: {
            light: '300',
            regular: '400',
            bold: '700',
        },
    },
    spacing: {
        xs: 4,
        sm: 8,
        md: 12,
        lg: 16,
        xl: 20,
        xxl: 24,
        xxxl: 32,
        huge: 48,
    },
    borderRadius: {
        small: 4,
        medium: 8,
        large: 12,
        xl: 16,
        xxl: 20,
        huge: 32,
    },
    shadows: {
        small: {
            shadowColor: '#000',
            shadowOffset: { width: 0, height: 1 },
            shadowOpacity: 0.1,
            shadowRadius: 2,
            elevation: 2,
        },
        medium: {
            shadowColor: '#000',
            shadowOffset: { width: 0, height: 2 },
            shadowOpacity: 0.2,
            shadowRadius: 4,
            elevation: 5,
        },
    },
};

export type Theme = typeof theme;