package com.example.mylibrary.model;

import com.example.mylibrary.math.Vector3;

import java.util.Arrays;

public final class VertexesData {
    // координаты вершин
    public float[] vertexes = null;
    // нормали вершин
    public float[] vertexes_normal = null;
    // координаты текстуры вершин
    public float[] vertexes_texture = null;
    // координаты карты нормалей вершин
    public float[] vertexes_normalTexture = null;

    // макс и мин точки
    public Vector3 maxPoint = null;
    public Vector3 minPoint = null;

    @Override
    public String toString() {
        return "VertexesData"
                +'\n' + "vertexes: " + Arrays.toString(vertexes)
                +'\n' + "vertexes_normal: " + Arrays.toString(vertexes_normal)
                +'\n' + "vertexes_texture: " + Arrays.toString(vertexes_texture)
                +'\n' + "vertexes_normalTexture: " + Arrays.toString(vertexes_normalTexture)
                +'\n' + "minPoint: " + minPoint
                +'\n' + "maxPoint: " + maxPoint;
    }
}
