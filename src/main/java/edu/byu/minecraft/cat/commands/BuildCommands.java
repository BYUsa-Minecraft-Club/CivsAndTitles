package edu.byu.minecraft.cat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import edu.byu.minecraft.cat.CivsAndTitles;
import edu.byu.minecraft.cat.commands.interactive.*;
import edu.byu.minecraft.cat.commands.interactive.parameters.*;
import edu.byu.minecraft.cat.dataaccess.*;
import edu.byu.minecraft.cat.model.*;
import edu.byu.minecraft.cat.util.Utilities;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.nbt.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;


import java.util.*;
import java.util.function.Function;

public class BuildCommands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("build").requires(ServerCommandSource::isExecutedByPlayer)
                .then(literal("request")
                        .then(argument("civName", StringArgumentType.greedyString()).suggests(SuggestionProviders::allCivs).executes(BuildCommands::buildJudgeRequest)))
                .then(literal("cancel")
                        .then(argument("buildId",  IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myBuildRequests).executes(BuildCommands::cancelJudgeRequest)))
                .then(literal("listRequests").executes(BuildCommands::listRequests))
                .then(literal("request_detailed")
                        .then(literal("raw").then(argument("data", NbtCompoundArgumentType.nbtCompound()).executes(BuildCommands::buildJudgeRequestRaw)))
                )
        );

        dispatcher.register(literal("build").requires(ServerCommandSource::isExecutedByPlayer).then(literal("judge").requires(PermissionCheckers::isBuildJudge)
                .then(literal("listActive").executes(BuildCommands::listActiveRequest))
                .then(literal("judgeMode").executes(BuildCommands::judgeMode))
                .then(literal("submitScore").executes(BuildCommands::submitScore))
        ));

        Function<CommandContext<ServerCommandSource>, int[]> defaultPosition = new Function<CommandContext<ServerCommandSource>, int[]>() {
            @Override
            public int[] apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());
                return new int[] {buildLocation.x(), buildLocation.y(), buildLocation.z()};
            }
        };

        Function<CommandContext<ServerCommandSource>, Object> defaultPosition2 = new Function<CommandContext<ServerCommandSource>, Object>() {
            @Override
            public Object apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());

                return new BlockPos(buildLocation.x(), buildLocation.y(), buildLocation.z());
            }
        };

        Function<CommandContext<ServerCommandSource>, String> getDefaultWorld = new Function<CommandContext<ServerCommandSource>, String>() {
            @Override
            public String apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());
                return buildLocation.world().toString();
            }
        };
        Function<CommandContext<ServerCommandSource>, Object> getDefaultWorld2 = new Function<CommandContext<ServerCommandSource>, Object>() {
            @Override
            public Object apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());
                return buildLocation.world();
            }
        };
        Function<CommandContext<ServerCommandSource>, NbtList> getDefaultRotation = new Function<CommandContext<ServerCommandSource>, NbtList>() {
            @Override
            public NbtList apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());
                NbtList list = new NbtList();
                list.add(0, NbtFloat.of(buildLocation.yaw()));
                list.add(1, NbtFloat.of(buildLocation.pitch()));
                return list;
            }
        };

        Function<CommandContext<ServerCommandSource>, Object> getDefaultRotation2 = new Function<CommandContext<ServerCommandSource>, Object>() {
            @Override
            public Object apply(CommandContext<ServerCommandSource> ctx) {
                Location buildLocation = Utilities.getPlayerLocation(ctx.getSource().getPlayer());
                return new Vec2f(buildLocation.yaw(), buildLocation.pitch());
            }
        };

        new InteractiveManager(Arrays.asList("build", "interactive"))
                .addLine(new InteractiveTextLine(Text.literal("Build Request")))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Name")))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("CivName").setSuggestionProvider(SuggestionProviders::allCivs).setValidater((x)-> {
                    try {
                        return CivsAndTitles.getDataAccess().getCivDAO().getForName((String)x) != null;
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e); // TODO what is the best think to handle in this error case
                    }
                })))
                .addLine(new InteractiveParameterLine(new InteractiveStringParameter("Comments", true)))
                .addLine(new InteractiveParameterLine(new InteractiveCoordinatesParameter("Pos").setDefaultProvider(defaultPosition2)))
                .addLine(new InteractiveParameterLine(new InteractiveRotationParameter("Rotation").setDefaultProvider(getDefaultRotation2)))
                .addLine(new InteractiveParameterLine(new InteractiveDimensionParameter("Dimension").setDefaultProvider(getDefaultWorld2)))
                .addLine(new InteractiveParameterLine(new InteractiveStringListParameter("Builders").setValidater((x)->{
                    List<String> usernames = (List<String>) x;
                    try {
                        for(String user: usernames)
                        {
                            if(CivsAndTitles.getDataAccess().getPlayerDAO().getPlayerUUID(user) == null)
                            {
                                return false;
                            }
                        }
                        return true;
                    } catch (DataAccessException e) {
                       return false;
                    }
                })))
                .addLine(new InteractiveFinishLine()).setDataHandler(BuildCommands::finishBuildJudgeRequest).register(dispatcher);

        new InteractiveDisplay<Integer, Build>(Arrays.asList("build", "display"), new InteractiveDisplay.DisplayProvider<Integer, Build>() {
            @Override
            public Text getSimpleText(Build build) {
                return Text.literal("(" + build.ID() + ")" + build.name() + ": " + build.status());
            }

            @Override
            public Text getDetailedText(Build build) {
                return Text.literal("(" + build.ID() + ")" + build.name() + ": " + build.status() + "\nSubmitted:" + build.submittedDate()) ;
            }

            @Override
            public Collection<Build> getValues(CommandContext<ServerCommandSource> ctx) {
                BuildDAO buildDAO;
                try {
                    buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
                    return buildDAO.getAllForSubmitter(ctx.getSource().getPlayer().getUuid());
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
                }
                return null;
            }

            @Override
            public Collection<Integer> getKeys(CommandContext<ServerCommandSource> ctx) {
                BuildDAO buildDAO;
                try {
                    buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
                    return buildDAO.getAllForSubmitter(ctx.getSource().getPlayer().getUuid()).stream().map(Build::ID).toList();
                } catch (DataAccessException e) {
                    ctx.getSource().sendFeedback(() -> Text.literal("Unable to access the database. Try again later."), false);
                }
                return null;
            }

            @Override
            public Build getValue(Integer key) {
                BuildDAO buildDAO;
                try {
                    buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
                    return buildDAO.get(key);
                } catch (DataAccessException e) {
                    return null;
                }
            }

            @Override
            public Integer getKey(Build value) {
                return value.ID();
            }
        }, new InteractiveDisplay.KeyInfo<Integer>() {
            @Override
            public Integer extractKey(CommandContext<ServerCommandSource> ctx) {
                return ctx.getArgument("buildId", Integer.class);
            }

            @Override
            public String getKeyName() {
                return "buildId";
            }

            @Override
            public ArgumentType<?> getArgumentType() {
                return IntegerArgumentType.integer();
            }
        }).register(dispatcher);
    }

    public static Integer buildJudgeRequest(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        CivDAO civDAO;
        BuildDAO buildDAO;
        BuilderDAO builderDAO;
        try {
            civDAO = CivsAndTitles.getDataAccess().getCivDAO();
            buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
            builderDAO = CivsAndTitles.getDataAccess().getBuilderDAO();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        Collection<Civ> civs;
        try {
            civs = civDAO.getAll();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        int civId = -1;
        for (Civ civ : civs) {
            if (civName.equalsIgnoreCase(civ.name())) {
               // ctx.getSource().sendFeedback(()->Text.literal("A civ with the name " + civName + " already exists."), false);
                civId = civ.ID();
            }
        }
        if (civId == -1)
        {
            ctx.getSource().sendFeedback(()->Text.literal("No civ with the name " + civName + " exists."), false);
            return 0;
        }

        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Location buildLocation = Utilities.getPlayerLocation(player);
            int locationId = CivsAndTitles.getDataAccess().getLocationDAO().insert(buildLocation);
            //TODO add support for other details
            Build build = new Build(0,"newBuild", Utilities.getTime(), locationId, civId, player.getUuid(), "", -1, -1, Build.JudgeStatus.PENDING);

            int id = buildDAO.insert(build);
            // TODO get builders other ways
            Builder builder = new Builder(id, player.getUuid());
            builderDAO.insert(builder);

            ctx.getSource().sendFeedback(()->Text.literal("Creating a new build request for " + civName + "with ID" + id), false);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }


        ctx.getSource().sendFeedback(()->Text.literal("Build Request for civ " + civName), false);
        //TODO
        return 1;
    }
    private static Integer finishBuildJudgeRequest(CommandContext<ServerCommandSource> ctx, Map<String, Object> parameters){
        String name = (String)parameters.get("Name");
        String civName = (String)parameters.get("CivName");
        String comments = (String)parameters.get("Comments");
        BlockPos pos = (BlockPos) parameters.get("Pos");
        Vec2f rot = (Vec2f) parameters.get("Rotation");
        Identifier dimension = (Identifier) parameters.get("Dimension");
        List<String> builders = (List<String>) parameters.get("Builders");
        Location location = new Location(0, pos.getX(), pos.getY(), pos.getZ(), dimension, rot.x, rot.y);
        return submitBuildRequest(ctx, name, civName, comments, location, builders);
    }

    private static Integer submitBuildRequest(CommandContext<ServerCommandSource> ctx, String buildName, String civName, String comments, Location location, List<String> builders)
    {
        CivDAO civDAO;
        BuildDAO buildDAO;
        BuilderDAO builderDAO;
        PlayerDAO playerDAO;
        try {
            civDAO = CivsAndTitles.getDataAccess().getCivDAO();
            buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
            builderDAO = CivsAndTitles.getDataAccess().getBuilderDAO();
            playerDAO = CivsAndTitles.getDataAccess().getPlayerDAO();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }

        Collection<Civ> civs;
        try {
            civs = civDAO.getAll();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        int civId = -1;
        for (Civ civ : civs) {
            if (civName.equalsIgnoreCase(civ.name())) {
                civId = civ.ID();
            }
        }
        if (civId == -1)
        {
           ctx.getSource().sendFeedback(()->Text.literal("No civ with the name " + civName + " exists."), false);
            return 0;
        }
        ArrayList<UUID> builderUUIDs = new ArrayList<>();
        for (String builder: builders){
            try {
                UUID uuid = playerDAO.getPlayerUUID(builder);
                builderUUIDs.add(uuid);
            } catch (DataAccessException e){
                ctx.getSource().sendFeedback(()->Text.literal("Failed to find UUID for player " + builder), false);
                return 0;
            }
        }

        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            if(location == null) {
                location = Utilities.getPlayerLocation(player);
            }
            int locationId = CivsAndTitles.getDataAccess().getLocationDAO().insert(location);
            Build build = new Build(0,buildName, Utilities.getTime(), locationId, civId, player.getUuid(), comments, -1, -1, Build.JudgeStatus.PENDING);

            int id = buildDAO.insert(build);
            for (UUID builderId: builderUUIDs) {
                Builder builder = new Builder(id, builderId);
                builderDAO.insert(builder);
            }

            ctx.getSource().sendFeedback(()->Text.literal("Creating a new build request for " + civName + "with ID" + id), false);
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }


        ctx.getSource().sendFeedback(()->Text.literal("Build Request for civ " + civName), false);
        return 1;
    }


    public static Integer buildJudgeRequestRaw(CommandContext<ServerCommandSource> ctx) {
        NbtCompound data = ctx.getArgument("data", NbtCompound.class);

        String civName = data.getString("CivName");
        String buildName = data.getString("Name");
        if (buildName.isEmpty())
        {
            ctx.getSource().sendFeedback(()->Text.literal("Name not provided"), false);
            return 0;
        }
        String comments;

        if (data.contains("Comments")) {
             comments = data.getString("Comments");
        } else {
            comments = null;
        }
        Location location = null;
        int[] posList = null;
        NbtList orientList = null;
        String dimension = null;
        if (data.contains("Pos")){
            posList = data.getIntArray("Pos");
        }
        if (data.contains("Rotation"))
        {
            orientList = data.getList("Rotation", NbtElement.FLOAT_TYPE);
        }
        if(data.contains("Dimension")){
            dimension =  data.getString("Dimension");
        }

        if (posList != null || orientList != null || dimension != null)
        {
            if(posList == null)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Incomplete location, missing position"), false);
                return 0;
            }
            if(dimension == null)
            {
                ctx.getSource().sendFeedback(()->Text.literal("Incomplete dimension, missing position"), false);
                return 0;
            }
            if (orientList == null)
            {
                location = new Location(0, posList[0],posList[1], posList[2], new Identifier(dimension),0,0);
            }
            else
            {
                location = new Location(0, posList[0],posList[1], posList[2], new Identifier(dimension),orientList.getFloat(0),orientList.getFloat(1));
            }
        }
        NbtList builders = data.getList("builders", NbtElement.STRING_TYPE);
        List<String> builderNames = new ArrayList<>();
        for (var builder: builders){
            builderNames.add(builder.asString());
        }
        return submitBuildRequest(ctx, buildName, civName, comments, location, builderNames);
    }


    public static Integer cancelJudgeRequest(CommandContext<ServerCommandSource> ctx) {
        Integer buildId = ctx.getArgument("buildId", Integer.class);
        ctx.getSource().sendFeedback(()->Text.literal("Cancel Build Request " + buildId), false);
        //TODO
        return 1;
    }

    public static Integer listRequests(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Listing Build Requests"), false);
        BuildDAO buildDAO;
        BuilderDAO builderDAO;
        try {
            buildDAO = CivsAndTitles.getDataAccess().getBuildDAO();
            builderDAO = CivsAndTitles.getDataAccess().getBuilderDAO();
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            Collection<Build> builds = buildDAO.getAllForSubmitter(player.getUuid());
            for (Build build: builds){
                ctx.getSource().sendFeedback(()->Text.literal("Build " + build.ID() + ": "+ build.name()),false);
            }
        } catch (DataAccessException e) {
            ctx.getSource().sendFeedback(()->Text.literal("Unable to access the database. Try again later."), false);
            return 0;
        }
        return 1;
    }


    // Build Judge Commands
    public static Integer listActiveRequest(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Listing Active Requests"), false);
        return 1;
    }

    public static Integer judgeMode(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Activating Judge Mode"), false);
        return 1;
    }

    public static Integer submitScore(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(()->Text.literal("Submitting score for build"), false);
        return 1;
    }



}
