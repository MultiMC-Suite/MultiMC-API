package fr.multimc.api.spigot.tools.dispatcher;

import fr.multimc.api.spigot.tools.utils.random.MmcRandom;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Dispatcher {

    private final DispatchAlgorithm dispatchAlgorithm;

    public Dispatcher(@NotNull DispatchAlgorithm dispatchAlgorithm){
        this.dispatchAlgorithm = dispatchAlgorithm;
    }

    public <A, B> Map<A, B> dispatch(@NotNull List<A> keys, @NotNull List<B> values){
        Map<A, B> finalMap = new HashMap<>();
        switch (dispatchAlgorithm){
            case ROUND_ROBIN -> {
                for(int i = 0; i < keys.size(); i++){
                    finalMap.put(keys.get(i), values.get(i % (values.size())));
                }
            }
            case REVERSED_ROUND_ROBIN -> {
                for(int i = keys.size() - 1; i >= 0; i--){
                    finalMap.put(keys.get(i), values.get(i % (values.size())));
                }
            }
            case RANDOM -> {
                for(A key: keys){
                    finalMap.put(key, values.get(new MmcRandom().nextInt(values.size())));
                }
            }
            case RANDOM_UNIQUE -> {
                if(keys.size() > values.size())
                    throw new RuntimeException("The number of keys must be less than or equal to the number of values (%d keys provided, %d values provided)"
                            .formatted(keys.size(), values.size()));
                List<B> valuesCopy = new ArrayList<>(values);
                int random;
                for(A key: keys){
                    random = new MmcRandom().nextInt(valuesCopy.size());
                    finalMap.put(key, valuesCopy.get(random));
                    valuesCopy.remove(random);
                }
            }
            default -> throw new RuntimeException("Unknown dispatch algorithm");
        }
        return finalMap;
    }
}
