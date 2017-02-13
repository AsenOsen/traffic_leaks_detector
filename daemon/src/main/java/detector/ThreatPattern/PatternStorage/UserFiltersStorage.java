package detector.ThreatPattern.PatternStorage;

import detector.ThreatPattern.Pattern.ThreatPattern;
import detector.UserDataManagers.UserFiltersManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

/**
 * Created by SAMSUNG on 12.02.2017.
 */
public class UserFiltersStorage extends PatternStorage
{

    @Nullable
    @Override
    protected InputStream getContentInputStream()
    {
        return null;
    }


    @NotNull
    @Override
    public List<ThreatPattern> getItems()
    {
        return UserFiltersManager.getInstance().getUserFilters();
    }

}
