package com.example;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import static java.util.stream.Collectors.joining;

import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.Charsets;
import org.apache.commons.math3.util.Combinations;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App
{
    


  public static SevenZFile decompress(File file, String password) throws IOException {
    byte[] passwordBytes = password.getBytes(Charsets.UTF_16LE);
    return new SevenZFile(file, passwordBytes);
  }
}