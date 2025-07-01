import { useState, useRef } from 'react';
import { Animated, Dimensions, PanResponder } from 'react-native';

export const useSwipeAnimation = (
    itemCount: number,
    currentPage: number,
    hasNext: boolean,
    onNext: () => void,
    onPrevious: () => void
) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const pan = useRef(new Animated.ValueXY()).current;

    const panResponder = PanResponder.create({
        onStartShouldSetPanResponder: () => true,
        onPanResponderMove: Animated.event(
            [null, { dx: pan.x }],
            { useNativeDriver: false }
        ),
        onPanResponderRelease: (e, gesture) => {
            if (gesture.dx > 120) {
                handleSwipe('right');
            } else if (gesture.dx < -120) {
                handleSwipe('left');
            } else {
                Animated.spring(pan, {
                    toValue: { x: 0, y: 0 },
                    useNativeDriver: false
                }).start();
            }
        }
    });

    const handleSwipe = (direction: 'left' | 'right') => {
        Animated.timing(pan, {
            toValue: {
                x: direction === 'right' ? Dimensions.get('window').width : -Dimensions.get('window').width,
                y: 0
            },
            duration: 250,
            useNativeDriver: false
        }).start(() => {
            pan.setValue({ x: 0, y: 0 });
            if (direction === 'left' && currentIndex < itemCount - 1) {
                setCurrentIndex(currentIndex + 1);
            } else if (direction === 'right' && currentIndex > 0) {
                setCurrentIndex(currentIndex - 1);
            } else if (direction === 'left' && hasNext) {
                onNext();
                setCurrentIndex(0);
            } else if (direction === 'right' && currentPage > 0) {
                onPrevious();
                setCurrentIndex(itemCount - 1);
            }
        });
    };

    return {
        currentIndex,
        pan,
        panResponder,
        setCurrentIndex
    };
};