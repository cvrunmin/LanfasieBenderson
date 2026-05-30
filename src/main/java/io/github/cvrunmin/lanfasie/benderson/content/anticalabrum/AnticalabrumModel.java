package io.github.cvrunmin.lanfasie.benderson.content.anticalabrum;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Tuple;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import net.neoforged.neoforge.client.textures.UnitTextureAtlasSprite;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.IOException;
import java.util.*;

public class AnticalabrumModel {
    public static final StandaloneModelKey<AnticalabrumModel> MODEL_KEY = new StandaloneModelKey<>(() -> "AnticalabrumModel");
    private static final Identifier ANTICALABRUM_TEXTURE_ALPHA = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/anticalabrum/texture_alpha.png");
    public static final Identifier ANTICALABRUM_TEXTURE = Identifier.fromNamespaceAndPath(LanfasieBenderson.MODID, "textures/entity/anticalabrum/texture.png");
    private final QuadCollection quads;
    private HashMap<Anticalabrum.AnticalabrumType, Tuple<BakedQuad, BakedQuad>> bakedFacesPerSwordType;

    public AnticalabrumModel(QuadCollection quadCollection, HashMap<Anticalabrum.AnticalabrumType, Tuple<BakedQuad, BakedQuad>> bakedFacesPerSwordType){
        this.quads = quadCollection;
        this.bakedFacesPerSwordType = bakedFacesPerSwordType;
    }

    public List<BakedQuad> getQuadsByState(Anticalabrum.AnticalabrumType anticalabrumType){
        var quads = new ArrayList<>(this.quads.getAll());
        if(bakedFacesPerSwordType.containsKey(anticalabrumType)){
            var pair = bakedFacesPerSwordType.get(anticalabrumType);
            quads.add(pair.getA());
            quads.add(pair.getB());
        }
        if(anticalabrumType != Anticalabrum.AnticalabrumType.EMPTY && bakedFacesPerSwordType.containsKey(Anticalabrum.AnticalabrumType.EMPTY)){
            var pair = bakedFacesPerSwordType.get(Anticalabrum.AnticalabrumType.EMPTY);
            quads.add(pair.getA());
            quads.add(pair.getB());
        }
        return quads;
    }

    public static UnbakedStandaloneModel<AnticalabrumModel> getBaker(){
        return new UnbakedStandaloneModel<>() {
            @Override
            public AnticalabrumModel bake(ModelBaker baker, ModelDebugName name) {
                var builder = new QuadCollection.Builder();
                var map = new HashMap<Anticalabrum.AnticalabrumType, Tuple<BakedQuad, BakedQuad>>();
                var resourceManager = Minecraft.getInstance().getResourceManager();
                try {
                    var texture = TextureContents.load(resourceManager, ANTICALABRUM_TEXTURE_ALPHA);
                    var vertexFrom = new Vector3f(-48, -7.5f, -0.5f);
                    var vertexTo = new Vector3f(16, 8.5f, 0.5f);
                    var uvFrom = new Vector2f(0, 0);
                    var uvTo = new Vector2f(1, 0.25f);
                    var matInfo = new BakedQuad.MaterialInfo(UnitTextureAtlasSprite.INSTANCE, ChunkSectionLayer.CUTOUT, RenderTypes.entityCutout(ANTICALABRUM_TEXTURE), 0, true, 0, true);
                    builder.addUnculledFace(bakeFace(baker.interner(), vertexFrom, vertexTo, uvFrom, uvTo, Direction.SOUTH, false, matInfo));
                    builder.addUnculledFace(bakeFace(baker.interner(), vertexFrom, vertexTo, uvFrom, uvTo, Direction.NORTH, false, matInfo));
                    bakeSideFaces(builder, texture.image(), baker.interner(), matInfo);
                    var tvFrom = new Vector3f(-40, -7f, -0.500f);
                    var tvTo = new Vector3f(-24, 9f, 0.500f);
                    var tuvFrom = new Vector2f();
                    var tuvTo = new Vector2f();
                    for (Anticalabrum.AnticalabrumType anticalabrumType : Anticalabrum.AnticalabrumType.values()) {
                        tuvFrom.set(anticalabrumType.getInfo().u(), anticalabrumType.getInfo().v()).div(128f);
                        tuvTo.set(anticalabrumType.getInfo().u() + 32, anticalabrumType.getInfo().v() + 32).div(128f);
                        if(anticalabrumType == Anticalabrum.AnticalabrumType.EMPTY){
                            var atvFrom = new Vector3f(-40, -7f, -0.5051f);
                            var atvTo = new Vector3f(-24, 9f, 0.5051f);
                            var f1 = bakeFace(baker.interner(), atvFrom, atvTo, tuvFrom, tuvTo, Direction.SOUTH, true, matInfo);
                            var f2 = bakeFace(baker.interner(), atvFrom, atvTo, tuvFrom, tuvTo, Direction.NORTH, true, matInfo);
                            map.put(anticalabrumType, new Tuple<>(f1, f2));
                        }else{
                            var f1 = bakeFace(baker.interner(), tvFrom, tvTo, tuvFrom, tuvTo, Direction.SOUTH, true, matInfo);
                            var f2 = bakeFace(baker.interner(), tvFrom, tvTo, tuvFrom, tuvTo, Direction.NORTH, true, matInfo);
                            map.put(anticalabrumType, new Tuple<>(f1, f2));
                        }
                    }
                } catch (IOException e) {

                }
                return new AnticalabrumModel(builder.build(), map);
            }

            @Override
            public void resolveDependencies(Resolver resolver) {

            }
        };
    }

    private static BakedQuad bakeFace(ModelBaker.Interner interner,
                                      Vector3fc from,
                                      Vector3fc to,
                                      Vector2fc uvFrom,
                                      Vector2fc uvTo,
                                      Direction facing,
                                      boolean iconMode,
                                      BakedQuad.MaterialInfo materialInfo){
        Vector3fc[] vertexPositions = new Vector3fc[4];
        long[] vertexPackedUvs = new long[4];
        FaceInfo faceInfo = FaceInfo.fromFacing(facing);
        Vector3fc uvFrom3;
        Vector3fc uvTo3;
        if(iconMode){
            uvFrom3 = new Vector3f(uvFrom.y(), uvFrom.x(), 0);
            uvTo3 = new Vector3f(uvTo.y(), uvTo.x(), 0);
        }else{
            uvFrom3 = new Vector3f(uvFrom, 0);
            uvTo3 = new Vector3f(uvTo, 0);
        }
        for (int i = 0; i < 4; i++) {
            FaceInfo.VertexInfo vertexInfo = faceInfo.getVertexInfo(i);
            Vector3f vertex = vertexInfo.select(from, to).div(16.0F);
            Vector3f uv = vertexInfo.select(uvFrom3, uvTo3);
            vertexPositions[i] = interner.vector(vertex);
            if(iconMode){
                vertexPackedUvs[i] = UVPair.pack(uv.y, uv.x);
            }
            else {
                vertexPackedUvs[i] = UVPair.pack(uv.x, uv.y);
            }
        }

        return new BakedQuad(
                vertexPositions[0],
                vertexPositions[1],
                vertexPositions[2],
                vertexPositions[3],
                vertexPackedUvs[0],
                vertexPackedUvs[1],
                vertexPackedUvs[2],
                vertexPackedUvs[3],
                facing,
                materialInfo,
                interner.normals(net.neoforged.neoforge.client.model.quad.BakedNormals.of(net.neoforged.neoforge.client.model.quad.BakedNormals.computeQuadNormal(vertexPositions[0], vertexPositions[1], vertexPositions[2], vertexPositions[3]))),
                interner.colors(BakedColors.DEFAULT)
        );
    }

    private static void bakeSideFaces(QuadCollection.Builder builder, NativeImage image, ModelBaker.Interner interner, BakedQuad.MaterialInfo matInfo){
        var scaleX = 64f / image.getWidth();
        var scaleY = 64f / image.getHeight();
        Vector3f from = new Vector3f();
        Vector3f to = new Vector3f();
        Vector2f uvFrom = new Vector2f();
        Vector2f uvTo = new Vector2f();
        for (RawSideFace sideFace : collectSideFaces(image)) {
            int pixelX = sideFace.x;
            int pixelY = sideFace.y;
            int rawVoxelY = 32 - pixelY;
            var facing = sideFace.facing;
            float u0 = pixelX + 0.1F;
            float u1 = pixelX + 1.0F - 0.1F;
            float v0;
            float v1;
            if (facing.direction.getAxis() == Direction.Axis.Y) {
                v0 = pixelY + 0.1F;
                v1 = pixelY + 1.0F - 0.1F;
            } else {
                v0 = pixelY + 1.0F - 0.1F;
                v1 = pixelY + 0.1F;
            }
            float startX = pixelX;
            float startY = rawVoxelY;
            float endX = pixelX;
            float endY = rawVoxelY;
            if(facing.isHorizontalFace()){
                endX += 1;
                if(facing == SideDirection.UP){
                    startY += 1;
                    endY += 1;
                }
            }else{
                endY += 1;
                if(facing == SideDirection.RIGHT){
                    startX += 1;
                    endX += 1;
                }
            }
            startX *= scaleX;
            endX *= scaleX;
            startY *= scaleY;
            endY *= scaleY;
            from.set(startX - 48, startY - 7.5, -0.5F);
            to.set(endX - 48, endY - 7.5, 0.5F);
            uvFrom.set(u0 / 128f, v0 / 128f);
            uvTo.set(u1 / 128f, v1 / 128f);
            builder.addUnculledFace(bakeFace(interner, from, to, uvFrom, uvTo, facing.direction, false, matInfo));
        }
    }

    private static HashSet<RawSideFace> collectSideFaces(NativeImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int candidateHeight = (int) (height / 128f) * 32;
        var sideFaces = new HashSet<RawSideFace>();
        for (int y = 0; y < candidateHeight; y++) {
            for (int x = 0; x < width; x++) {
                var opaque = !isTransparent(image, x, y, width, height);
                if(opaque){
                    createSideFaceIfNeeded(SideDirection.UP, sideFaces, image, x, y, width, height);
                    createSideFaceIfNeeded(SideDirection.DOWN, sideFaces, image, x, y, width, height);
                    createSideFaceIfNeeded(SideDirection.LEFT, sideFaces, image, x, y, width, height);
                    createSideFaceIfNeeded(SideDirection.RIGHT, sideFaces, image, x, y, width, height);
                }
            }
        }
        return sideFaces;
    }

    private static void createSideFaceIfNeeded(SideDirection direction, Set<RawSideFace> sideFaces, NativeImage image, int x, int y, int w, int h){
        if(isTransparent(image, x + direction.direction.getStepX(), y - direction.direction.getStepY(), w, h)){
            sideFaces.add(new RawSideFace(direction, x, y));
        }
    }


    private static boolean isTransparent(NativeImage image, int x, int y, int width, int height) {
        return x < 0 || y < 0 || x >= width || y >= height || ARGB.alpha(image.getPixel(x, y)) == 0;
    }

    private enum SideDirection {
        UP(Direction.UP), DOWN(Direction.DOWN), LEFT(Direction.WEST), RIGHT(Direction.EAST);
        public final Direction direction;

        SideDirection(Direction direction) {
            this.direction = direction;
        }

        public boolean isHorizontalFace(){
            return this == UP || this == DOWN;
        }
    }

    private record RawSideFace(SideDirection facing, int x, int y){}
}
