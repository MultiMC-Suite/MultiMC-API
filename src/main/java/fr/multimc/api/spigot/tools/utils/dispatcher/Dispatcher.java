package fr.multimc.api.spigot.tools.utils.dispatcher;

import fr.multimc.api.spigot.tools.utils.random.MmcRandom;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class Dispatcher {

    private final DispatchAlgorithm dispatchAlgorithm;

    public Dispatcher(@NotNull DispatchAlgorithm dispatchAlgorithm){
        this.dispatchAlgorithm = dispatchAlgorithm;
    }

    @Nullable
    public <A, B> Map<A, B> dispatch(@NotNull List<A> keys, @NotNull List<B> values){
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
                    finalMap.put(key, values.get(new MmcRandom().nextInt(values.size())));
                }
            }
            case RANDOM_UNIQUE -> {
                if(keys.size() > values.size()) return null;
                List<B> valuesCopy = new ArrayList<>(values);
                int random;
                for(A key: keys){
                    random = new MmcRandom().nextInt(valuesCopy.size());
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
}
