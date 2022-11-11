package fr.multimc.api.spigot.tools.utils.dispatcher;

import java.util.*;

@SuppressWarnings("unused")
public class Dispatcher {

    private final DispatchAlgorithm dispatchAlgorithm;

    public Dispatcher(DispatchAlgorithm dispatchAlgorithm){
        this.dispatchAlgorithm = dispatchAlgorithm;
    }

    public <A, B> Map<A, B> dispatch(List<A> keys, List<B> values){
        Map<A, B> finalMap = new HashMap<>();
        switch (dispatchAlgorithm){
            case ROUND_ROBIN -> {
                for(int i = 0; i < keys.size(); i++){
                    finalMap.put(keys.get(i), values.get(i % (values.size() - 1)));
                }
            }
            case REVERSED_ROUND_ROBIN -> {
                for(int i = keys.size() - 1; i >= 0; i--){
                    finalMap.put(keys.get(i), values.get(i % (values.size() - 1)));
                }
            }
            case RANDOM -> {
                for(A key: keys){
                    finalMap.put(key, values.get(this.getRandomNumber(values.size())));
                }
            }
            case RANDOM_UNIQUE -> {
                if(keys.size() > values.size()) return null;
                List<B> valuesCopy = new ArrayList<>(values);
                int random;
                for(A key: keys){
                    random = this.getRandomNumber(valuesCopy.size());
                    finalMap.put(key, valuesCopy.get(random));
                    valuesCopy.remove(random);
                }
            }
            default -> {
                return null;
            }
        }
        return finalMap;
    }

    private int getRandomNumber(int max){
        return new Random().nextInt(max);
    }

    private int getRandomNumber(int min, int max){
        return new Random().nextInt(max - min) + min;
    }
}
