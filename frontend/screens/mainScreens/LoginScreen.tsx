import {LoginScreenType} from "../types/LoginScreenType";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Title} from "../css_styling/common/Typography";
import {Button, ButtonsContainer, ButtonText} from "../css_styling/common/Buttons";
import {Input} from "../css_styling/common/Inputs";
import {router} from "expo-router";


export const LoginScreen = ({ email, password, onEmailChange, onPasswordChange, onLogin, onNavigateToRegister } : LoginScreenType) => (
    <CenteredContainer flex={1} >
        <Card shadow="medium" padding="md" gap="md">
            <Title color="black">Login</Title>
            <Input
                placeholder="Email"
                value={email}
                onChangeText={onEmailChange}
                keyboardType="email-address"
                autoCapitalize="none"
                placeholderTextColor="#666"
            />
            <Input
                placeholder="Password"
                value={password}
                onChangeText={onPasswordChange}
                secureTextEntry
                placeholderTextColor="#666"
            />
            <ButtonsContainer gap="md">
                <Button
                    variant="primary"
                    size="medium"
                    onPress={onLogin}
                >
                    <ButtonText>Login</ButtonText>
                </Button>
                <Button
                    variant="primary"
                    size="medium"
                    onPress={onNavigateToRegister}
                >
                    <ButtonText>Go to Register</ButtonText>
                </Button>
                <Button
                    variant="primary"
                    onPress={() => router.push('/welcome')}
                >
                    <ButtonText>Go Back</ButtonText>
                </Button>
            </ButtonsContainer>
        </Card>
    </CenteredContainer>
);
